package com.xixi.netty.rpc.server;

import com.google.common.collect.Lists;
import com.xixi.netty.rpc.common.JsonSerializer;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
@Slf4j
public class DefaultMethodArgsConverter implements MethodArgsConverter {

    private static DefaultMethodArgsConverter instance;

    public static synchronized DefaultMethodArgsConverter getInstance() {
        if (instance == null) {
            instance = new DefaultMethodArgsConverter();
        }
        return instance;
    }

    @Override
    public Object[] argsConvert(ArgsConverterInput input) {
        if (input == null || CollectionUtils.isEmpty(input.getArguments())) {
            return new Object[0];
        }
        List<Class<?>> parameterType = Optional.ofNullable(input.getParameterType()).orElse(new ArrayList<>());

        int size = parameterType.size();
        if (size > 0) {
            return getArgs(parameterType, input.getArguments());
        }

        Class<?>[] parameterTypes = input.getMethod().getParameterTypes();
        return getArgs(Lists.newArrayList(parameterTypes), input.getArguments());

    }

    private Object[] getArgs(List<Class<?>> parameterType, List<Object> arguments) {
        JsonSerializer instance = JsonSerializer.getInstance();
        Object[] objects = new Object[parameterType.size()];
        for (int i = 0; i < parameterType.size(); i++) {
            ByteBuf byteBuf = (ByteBuf) arguments.get(i);
            int readableBytes = byteBuf.readableBytes();
            byte[] bytes = new byte[readableBytes];
            byteBuf.readBytes(bytes);
            objects[i] = instance.decode(bytes, parameterType.get(i));
            byteBuf.release();
        }
        return objects;
    }
}
