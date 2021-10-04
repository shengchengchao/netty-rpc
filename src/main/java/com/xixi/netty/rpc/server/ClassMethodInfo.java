package com.xixi.netty.rpc.server;

import lombok.Data;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
@Data
public class ClassMethodInfo {

    private Class<?> methodClass;
    private Class<?> userClass;
    private Object target;
}
