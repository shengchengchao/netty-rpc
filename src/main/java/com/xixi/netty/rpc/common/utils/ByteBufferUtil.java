package com.xixi.netty.rpc.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
public class ByteBufferUtil {


    public static void encodeUtf8CharSequence(ByteBuf byteBuf, CharSequence charSequence) {
        int writerIndex = byteBuf.writerIndex();
        byteBuf.writeInt(0);
        int length = ByteBufUtil.writeUtf8(byteBuf, charSequence);
        byteBuf.setInt(writerIndex, length);
    }

    public static byte[] readBytes(ByteBuf byteBuf) {
        int len = byteBuf.readableBytes();
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        byteBuf.release();
        return bytes;
    }
}
