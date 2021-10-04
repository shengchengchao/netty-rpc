package com.xixi.netty.rpc.protocol.encode;

import com.xixi.netty.rpc.protocol.message.ProtocolConstant;
import com.xixi.netty.rpc.protocol.message.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Optional;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/3
 */
public class RequestMessageEncode extends MessageToByteEncoder<RequestMessage> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage, ByteBuf byteBuf) throws Exception {
        // 加密基础数据
        requestMessage.encodeMessage(byteBuf);
        //请求数据加密  接口全路径
        byteBuf.writeInt(Optional.ofNullable(requestMessage.getInterfaceName()).orElse("").length());
        byteBuf.writeCharSequence(Optional.ofNullable(requestMessage.getInterfaceName()).orElse(""), ProtocolConstant.UTF_8);
        //方法名
        byteBuf.writeInt(Optional.ofNullable(requestMessage.getMethodName()).orElse("").length());
        byteBuf.writeCharSequence(Optional.ofNullable(requestMessage.getMethodName()).orElse(""), ProtocolConstant.UTF_8);
        //方法参数
        if (null != requestMessage.getMethodArgs() && requestMessage.getMethodArgs().length > 0) {
            int length = requestMessage.getMethodArgs().length;
            byteBuf.writeInt(length);
            for (String methodArg : requestMessage.getMethodArgs()) {
                byteBuf.writeInt(Optional.ofNullable(methodArg).orElse("").length());
                byteBuf.writeCharSequence(Optional.ofNullable(methodArg).orElse(""), ProtocolConstant.UTF_8);
            }
        }
        //方法参数类型
        if (null != requestMessage.getMethodArgsSignatures() && requestMessage.getMethodArgsSignatures().length > 0) {
            int length = requestMessage.getMethodArgsSignatures().length;
            byteBuf.writeInt(length);
            for (String methodArgClass : requestMessage.getMethodArgsSignatures()) {
                byteBuf.writeInt(Optional.ofNullable(methodArgClass).orElse("").length());
                byteBuf.writeCharSequence(Optional.ofNullable(methodArgClass).orElse(""), ProtocolConstant.UTF_8);
            }
        }
    }
}
