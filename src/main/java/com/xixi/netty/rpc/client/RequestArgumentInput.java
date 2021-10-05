package com.xixi.netty.rpc.client;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/5
 */
@Data
public class RequestArgumentInput {

    private Class<?> interfaceClass;

    private Method method;


}
