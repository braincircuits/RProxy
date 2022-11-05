import handler.RProxyClientChannelInit;
import handler.RProxyServerChannelHandler;
import handler.ServerClientMessageHandler;
import init.Init;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Properties;

public class Server {
    public static void main(String[] args) {
        Properties properties = Init.Instance.properties;
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        ServerBootstrap clientB = new ServerBootstrap();
        clientB.group(group)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new RProxyClientChannelInit())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.AUTO_READ,false)
        ;
        clientB.bind(Integer.parseInt(properties.getProperty("port"))).addListener(future -> {
            System.out.println("启动成功1" + future.isSuccess());
        });
        //==============================================
        ServerBootstrap serverB = clientB.clone();
        serverB.childOption(ChannelOption.AUTO_READ, true);
        serverB.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new RProxyServerChannelHandler());
            }
        });
        serverB.bind(Integer.parseInt(properties.getProperty("proxyPort"))).addListener(future -> {
            System.out.println("启动成功2"+future.isSuccess());
        });

        //==============================================
        ServerBootstrap scB = clientB.clone();
        scB.childOption(ChannelOption.AUTO_READ, true);
        scB.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ServerClientMessageHandler());
            }
        });
        scB.bind(Integer.parseInt(properties.getProperty("messagePort"))).addListener(future -> {
            System.out.println("启动成功3"+future.isSuccess());
        });

    }
}
