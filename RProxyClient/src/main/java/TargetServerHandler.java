import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class TargetServerHandler extends ChannelInboundHandlerAdapter {
    public Channel serverChannel;
    EventLoopGroup group;

    public TargetServerHandler(EventLoopGroup group) {
        this.group = group;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx1, Object msg1) throws Exception {
        if (serverChannel != null) {
            serverChannel.writeAndFlush(msg1);
        } else {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx2, Object msg2) throws Exception {
                                    ctx1.channel().writeAndFlush(msg2);
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    ctx1.channel().close();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    System.out.println(cause.getMessage());
                                    ctx.channel().close();
                                }
                            });
                        }
                    });
            b.connect(new InetSocketAddress(Init.Instance.getProperties().getProperty("targetIP"), Integer.parseInt(Init.Instance.getProperties().getProperty("targetPort")))).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        Channel channel = future.channel();
                        serverChannel = channel;
                        channel.writeAndFlush(msg1);
                    } else {
                        serverChannel = null;
                    }
                }
            });
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (serverChannel != null) {
            serverChannel.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.channel().close();
    }
}
