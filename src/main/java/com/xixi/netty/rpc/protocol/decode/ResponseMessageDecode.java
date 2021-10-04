package com.xixi.netty.rpc.protocol.decode;

import com.xixi.netty.rpc.protocol.message.ProtocolConstant;
import com.xixi.netty.rpc.protocol.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
public class ResponseMessageDecode extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.decodeMessage(in);

        responseMessage.setResponseCode(in.readLong());

        int messageLength = in.readInt();
        responseMessage.setMessage(in.readCharSequence(messageLength, ProtocolConstant.UTF_8).toString());

        int payLoadLength = in.readInt();

        responseMessage.setPayload(in.readBytes(payLoadLength));

        out.add(responseMessage);
    }
}
