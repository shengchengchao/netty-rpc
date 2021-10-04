package com.xixi.netty.rpc.server;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
@Data
public class ArgsConverterInput {

    /**
     * 方法
     */
    private Method method;
    /**
     * 参数类型
     */
    private List<Class<?>> parameterType;

    /**
     * 参数
     */
    private List<Object> arguments;


}
