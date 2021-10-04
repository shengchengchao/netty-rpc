package com.xixi.netty.rpc.protocol.message;

import lombok.Data;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/3
 */
@Data
public class RequestMessage extends BaseMessage {

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数
     */
    private String[] methodArgs;

    /**
     * 方法参数类型类型数组
     */
    private String[] methodArgsType;


}
