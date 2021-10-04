package com.xixi.netty.rpc.protocol.decode;

import com.xixi.netty.rpc.protocol.message.ProtocolConstant;
import com.xixi.netty.rpc.protocol.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
public class RequestMessageDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.decodeMessage(byteBuf);

        int interfaceNameLength = byteBuf.readInt();
        requestMessage.setInterfaceName(byteBuf.readCharSequence(interfaceNameLength, ProtocolConstant.UTF_8).toString());

        int methodNameLength = byteBuf.readInt();
        requestMessage.setMethodName(byteBuf.readCharSequence(methodNameLength, ProtocolConstant.UTF_8).toString());

        int methodArgsArrayLength = byteBuf.readInt();
        if (methodArgsArrayLength > 0) {
            String[] methodArgsArray = new String[methodArgsArrayLength];
            for (int i = 0; i < methodArgsArrayLength; i++) {
                int argsLength = byteBuf.readInt();
                methodArgsArray[i] = byteBuf.readCharSequence(argsLength, ProtocolConstant.UTF_8).toString();
            }
            requestMessage.setMethodArgs(methodArgsArray);
        }

        int methodArgsTypeLength = byteBuf.readInt();
        if (methodArgsTypeLength > 0) {
            String[] methodArgsTypeArray = new String[methodArgsTypeLength];
            for (int i = 0; i < methodArgsTypeLength; i++) {
                int argsLength = byteBuf.readInt();
                methodArgsTypeArray[i] = byteBuf.readCharSequence(argsLength, ProtocolConstant.UTF_8).toString();
            }
            requestMessage.setMethodArgsSignatures(methodArgsTypeArray);
        }
    }
}
