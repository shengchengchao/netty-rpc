package com.xixi.netty.rpc;

import com.xixi.netty.rpc.common.JsonSerializer;
import com.xixi.netty.rpc.common.utils.ApplicationContextUtil;
import com.xixi.netty.rpc.protocol.decode.RequestMessageDecode;
import com.xixi.netty.rpc.protocol.encode.RequestMessageEncode;
import com.xixi.netty.rpc.server.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyRpcApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(NettyRpcApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        int port = 9092;


        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new RequestMessageDecode());
                        ch.pipeline().addLast(new RequestMessageEncode(JsonSerializer.getInstance()));
                        ch.pipeline().addLast(ApplicationContextUtil.getBeanByClass(ServerHandler.class));
                    }
                });
        ChannelFuture future = bootstrap.bind(port).sync();
        future.channel().closeFuture().sync();

    }
}
