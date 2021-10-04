package com.xixi.netty.rpc.protocol.enums;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/3
 */
public enum MessageType {

    /**
     * request
     */
    REQUEST("request", (byte) 1),
    /**
     * response
     */
    RESPONSE("response", (byte) 2),
    /**
     * PING
     */
    PING("ping", (byte) 3),

    /**
     * PONG
     */
    PONG("pong", (byte) 4),

    /**
     * NULL
     */
    NULL("null", (byte) 5),
    ;


    private String code;
    private Byte type;

    MessageType(String code, Byte type) {
        this.code = code;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public static Byte getTypeByCode(String code) {
        for (MessageType messageType : MessageType.values()) {
            if (code != null && messageType.getCode().equals(code)) {
                return messageType.getType();
            }
        }
        return null;
    }
}
