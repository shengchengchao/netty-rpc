package com.xixi.netty.rpc.server;

/**
 * 找到最匹配的一个方法
 *
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
public interface MethodMatch {

    MethodMatchOut selectBestMatchMethod(MethodMatchInput input);
}
