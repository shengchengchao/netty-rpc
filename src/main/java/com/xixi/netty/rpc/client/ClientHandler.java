package com.xixi.netty.rpc.client;

import com.xixi.netty.rpc.protocol.message.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/5
 */
public class ClientHandler extends SimpleChannelInboundHandler<ResponseMessage> {
    /**
     * Is called for each message of type {@link I}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
        ResponseFuture responseFuture = ContractFactory.RESPONSE_FUTURE_TABLE.get(msg.getSerialNumber());
        if (responseFuture != null) {
            responseFuture.putResponse(msg);
        }
    }
}
