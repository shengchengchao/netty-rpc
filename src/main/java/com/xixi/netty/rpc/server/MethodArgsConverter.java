package com.xixi.netty.rpc.server;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
public interface MethodArgsConverter {

    Object[] argsConvert(ArgsConverterInput input);
}
