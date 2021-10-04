package com.xixi.netty.rpc.server;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xixi.netty.rpc.protocol.enums.MessageType;
import com.xixi.netty.rpc.protocol.message.RequestMessage;
import com.xixi.netty.rpc.protocol.message.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
@Component
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<RequestMessage> {
    /**
     * Is called for each message of type {@link I}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        MethodMatchInput methodMatchInput = new MethodMatchInput();
        methodMatchInput.setInterfaceName(msg.getInterfaceName());
        methodMatchInput.setMethodName(msg.getMethodName());
        methodMatchInput.setMethodArgsLength(Optional.ofNullable(msg.getMethodArgs()).orElse(new String[0]).length);
        methodMatchInput.setMethodArgumentSignatures(Lists.newArrayList(Optional.ofNullable(msg.getMethodArgsSignatures()).orElse(new String[0])));

        log.info(" serverHandler.channelRead0  methodMatchInput 为 {}", methodMatchInput.toString());
        MethodMatchOut methodMatchOut = BestMethodMatch.getInstance().selectBestMatchMethod(methodMatchInput);
        if (methodMatchOut == null) {
            log.error(" ServerHandler.channelRead0 :发生异常,methodMatchOut为空 ");
        }
        log.info(" ServerHandler.channelRead0 methodMatchOut 为{} ", methodMatchOut.toString());

        Method targetMethod = methodMatchOut.getTargetMethod();
        ArgsConverterInput argsConverterInput = new ArgsConverterInput();
        argsConverterInput.setMethod(targetMethod);
        argsConverterInput.setArguments(Lists.newArrayList(Optional.ofNullable(msg.getMethodArgs()).orElse(new String[0])));
        argsConverterInput.setParameterType(methodMatchOut.getParameterTypes());
        Object[] objects = DefaultMethodArgsConverter.getInstance().argsConvert(argsConverterInput);
        ReflectionUtils.makeAccessible(targetMethod);

        Object result = targetMethod.invoke(methodMatchOut.getTarget(), objects);
        ResponseMessage response = applyResponseMessagePacket(msg);
        response.setPayload(result);
        log.info("服务端输出:{}", JSON.toJSONString(response));
        ctx.writeAndFlush(response);

    }

    private ResponseMessage applyResponseMessagePacket(RequestMessage packet) {

        ResponseMessage response = new ResponseMessage();
        response.setMagicNumber(packet.getMagicNumber());
        response.setVersion(packet.getVersion());
        response.setSerialNumber(packet.getSerialNumber());
        response.setAttachments(packet.getAttachments());
        response.setMessageType(MessageType.RESPONSE.getType());
        response.setResponseCode(200L);
        response.setMessage("Success");
        return response;


    }
}
