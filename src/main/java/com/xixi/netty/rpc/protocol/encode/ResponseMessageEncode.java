package com.xixi.netty.rpc.protocol.encode;

import com.xixi.netty.rpc.common.JsonSerializer;
import com.xixi.netty.rpc.common.utils.ByteBufferUtil;
import com.xixi.netty.rpc.protocol.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
public class ResponseMessageEncode extends MessageToByteEncoder<ResponseMessage> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage, ByteBuf byteBuf) throws Exception {

        // 加密基础数据
        responseMessage.encodeMessage(byteBuf);
        //返回状态
        byteBuf.writeLong(responseMessage.getResponseCode());
        String message = responseMessage.getMessage();
        ByteBufferUtil.encodeUtf8CharSequence(byteBuf, message);

        byte[] encode = JsonSerializer.getInstance().encode(responseMessage.getPayload());
        byteBuf.writeInt(encode.length);
        byteBuf.writeBytes(encode);
    }
}
