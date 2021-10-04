package com.xixi.netty.rpc.protocol.message;

import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 消息基本类型
 *
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/3
 */
@Data
public abstract class BaseMessage {

    /**
     * 魔数
     */
    private int magicNumber;

    /**
     * 版本
     */
    private int version;

    /**
     * 流水号
     */
    private String serialNumber;

    /**
     * 消息类型
     */
    private Byte messageType;


    /**
     * 附件 - K-V形式
     */
    private Map<String, String> attachments = new HashMap<>();

    /**
     * 添加附件
     */
    public void addAttachment(String key, String value) {
        attachments.put(key, value);
    }


    /**
     * 解码
     *
     * @param in
     */
    public void decodeMessage(ByteBuf in) {

        setMagicNumber(in.readInt());

        setVersion(in.readInt());

        int serialNumberLength = in.readInt();
        setSerialNumber(in.readCharSequence(serialNumberLength, ProtocolConstant.UTF_8).toString());

        setMessageType(in.readByte());

        Map<String, String> attachment = new HashMap<>();
        int attachmentSize = in.readInt();
        for (int i = 0; i < attachmentSize; i++) {
            int keyLength = in.readInt();
            String key = in.readCharSequence(keyLength, ProtocolConstant.UTF_8).toString();
            int valueLength = in.readInt();
            String value = in.readCharSequence(valueLength, ProtocolConstant.UTF_8).toString();
            attachment.put(key, value);
        }
        setAttachments(attachment);
    }

    /**
     * 加密
     *
     * @param out
     */
    public void encodeMessage(ByteBuf out) {

        out.writeInt(getMagicNumber());

        out.writeInt(getVersion());

        out.writeInt(Optional.ofNullable(getSerialNumber()).orElse("").length());

        out.writeCharSequence(Optional.ofNullable(getSerialNumber()).orElse(""), ProtocolConstant.UTF_8);

        out.writeByte(getMessageType());

        Map<String, String> attachments = getAttachments();
        attachments.forEach((key, value) -> {
            out.writeInt(key.length());
            out.writeCharSequence(key, ProtocolConstant.UTF_8);
            out.writeInt(value.length());
            out.writeCharSequence(value, ProtocolConstant.UTF_8);

        });


    }
}
