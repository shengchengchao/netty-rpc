package com.xixi.netty.rpc.common;

import com.alibaba.fastjson.JSON;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/3
 */
public class JsonSerializer implements Serializer {

    private static JsonSerializer instance;

    public static synchronized JsonSerializer getInstance() {
        if (instance == null) {
            instance = new JsonSerializer();
        }
        return instance;
    }

    @Override
    public byte[] encode(Object target) {
        return JSON.toJSONBytes(target);
    }

    @Override
    public Object decode(byte[] bytes, Class<?> targetClass) {
        return JSON.parseObject(bytes, targetClass);
    }
}
