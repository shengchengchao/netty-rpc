package com.xixi.netty.rpc.protocol.message;

import lombok.Data;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/3
 */
@Data
public class ResponseMessage extends BaseMessage {
    /**
     * 响应码
     */
    private Long ResponseCode;


    /**
     * 消息描述
     */
    private String message;

    /**
     * 负载
     */
    private Object payload;
}
