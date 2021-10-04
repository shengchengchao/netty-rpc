package com.xixi.netty.rpc.common;


/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/3
 */
public interface Serializer {

    byte[] encode(Object target);

    Object decode(byte[] bytes, Class<?> targetClass);
}
