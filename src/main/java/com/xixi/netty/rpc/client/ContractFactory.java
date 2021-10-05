package com.xixi.netty.rpc.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xixi.netty.rpc.common.JsonSerializer;
import com.xixi.netty.rpc.protocol.enums.MessageType;
import com.xixi.netty.rpc.protocol.message.ProtocolConstant;
import com.xixi.netty.rpc.protocol.message.RequestMessage;
import com.xixi.netty.rpc.protocol.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/5
 */
@Slf4j
public class ContractFactory {


    public static final Long REQUEST_MAX_TIME = 3000L;

    private static ExecutorService executorService;

    public static final ConcurrentMap<String /* request id */, ResponseFuture> RESPONSE_FUTURE_TABLE = Maps.newConcurrentMap();

    private static final ScheduledExecutorService CLIENT_HOUSE_KEEPER;

    /**
     * 线程数计数器
     */
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private static JsonSerializer jsonSerializer = JsonSerializer.getInstance();

    static {
        int n = Runtime.getRuntime().availableProcessors();
        executorService = new ThreadPoolExecutor(n * 2, n * 2, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50), runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("client-request-executor-" + COUNTER.getAndIncrement());
            return thread;
        });
        CLIENT_HOUSE_KEEPER = new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("client-house-keeper");
            return thread;
        });
        CLIENT_HOUSE_KEEPER.scheduleWithFixedDelay(ContractFactory::scanResponseFutureTable, 5, 5, TimeUnit.SECONDS);
    }

    static void scanResponseFutureTable() {
        log.info("开始执行ResponseFutureTable清理任务......");
        Iterator<Map.Entry<String, ResponseFuture>> iterator = RESPONSE_FUTURE_TABLE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ResponseFuture> entry = iterator.next();
            ResponseFuture responseFuture = entry.getValue();
            if (responseFuture.timeout()) {
                iterator.remove();
                log.warn("移除过期的请求ResponseFuture,请求ID:{}", entry.getKey());
            }
        }
        log.info("执行ResponseFutureTable清理任务结束......");
    }


    private static <T> T getClass(Class<T> interfaceClass) {


        Object o = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> {

            RequestMessage requestMessage = new RequestMessage();

            requestMessage.setMagicNumber(ProtocolConstant.MEGIC_NUBER);
            requestMessage.setVersion(ProtocolConstant.VERSION);
            requestMessage.setMethodName(method.getName());
            requestMessage.setInterfaceName(interfaceClass.getName());
            requestMessage.setMessageType(MessageType.REQUEST.getType());
            requestMessage.setSerialNumber(UUID.randomUUID().toString().replace("-", ""));
            requestMessage.setMethodArgs(args);

            List<String> collect = Optional.ofNullable(Lists.newArrayList(method.getParameterTypes())).orElse(new ArrayList<>())
                    .stream().map(Class::getName).collect(Collectors.toList());
            requestMessage.setMethodArgsSignatures(collect.toArray(new String[0]));

            Channel channel = ClientChannelHolder.channelReference.get();

            return sendRequestSync(channel, requestMessage, method.getReturnType());
        });
        return (T) o;

    }

    static Object sendRequestSync(Channel channel, RequestMessage packet, Class<?> returnType) {

        long startTime = System.currentTimeMillis();
        ResponseFuture responseFuture = new ResponseFuture(REQUEST_MAX_TIME, packet.getSerialNumber());
        RESPONSE_FUTURE_TABLE.put(packet.getSerialNumber(), responseFuture);

        try {
            Future<ResponseMessage> resultFuture = executorService.submit(() -> {
                channel.writeAndFlush(packet).addListener((ChannelFutureListener)
                        future -> responseFuture.setSendRequestSucceed(true));


                return responseFuture.waitResponse(REQUEST_MAX_TIME - (System.currentTimeMillis() - startTime));
            });
            ResponseMessage responseMessage = resultFuture.get(REQUEST_MAX_TIME - (System.currentTimeMillis() - startTime), TimeUnit.MILLISECONDS);
            if (responseMessage == null) {
                log.error(" ContractFactory.sendRequestSync :发生异常,获取超时");
            } else {
                ByteBuf payload = (ByteBuf) responseMessage.getPayload();
                int len = payload.readableBytes();
                byte[] bytes = new byte[len];
                payload.readBytes(bytes);
                payload.release();
                return jsonSerializer.decode(bytes, returnType);
            }

        } catch (Exception e) {
            log.error(" ContractFactory.sendRequestSync :发生异常", e);
        }
        return null;
    }
}
