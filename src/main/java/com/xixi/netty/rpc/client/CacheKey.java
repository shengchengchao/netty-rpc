package com.xixi.netty.rpc.client;

import lombok.Data;

import java.util.List;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/5
 */
@Data
public class CacheKey {

    private String interfaceName;
    private String methodName;
    private List<Class<?>> parameterTypes;

    public CacheKey(String interfaceName, String methodName, List<Class<?>> parameterTypes) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    public CacheKey() {
    }
}
