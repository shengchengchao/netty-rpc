package com.xixi.netty.rpc.client;

import com.xixi.netty.rpc.common.JsonSerializer;
import com.xixi.netty.rpc.protocol.decode.ResponseMessageDecode;
import com.xixi.netty.rpc.protocol.encode.RequestMessageEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/10
 */
@Slf4j
public class ClientApplication {

    public static void main(String[] args) {
        int port = 9092;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new ResponseMessageDecode());
                        ch.pipeline().addLast(new RequestMessageEncode(JsonSerializer.getInstance()));
                        ch.pipeline().addLast(new ClientHandler());

                    }
                });
        bootstrap.connect("127.0.0.1", port);

    }
}
