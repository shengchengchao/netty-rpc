package com.xixi.netty.rpc.client;

import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/5
 */
public class ClientChannelHolder {


    public static AtomicReference<Channel> channelReference = new AtomicReference<>();
}
