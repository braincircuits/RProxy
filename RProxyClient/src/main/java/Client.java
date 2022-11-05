import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf byteBuf = (ByteBuf) msg;
                        for (int i = 0; i < byteBuf.readableBytes(); i++) {
                            Bootstrap clone = b.clone();
                            clone.handler(new ChannelInitializer<>() {
                                @Override
                                protected void initChannel(Channel ch) throws Exception {
                                    ch.pipeline().addLast(new TargetServerHandler(group));
                                }
                            });
                            clone.remoteAddress(Init.Instance.getProperties().getProperty("proxyIP"), Integer.parseInt(Init.Instance.getProperties().getProperty("proxyPort")));
                            clone.connect().addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    if (!future.isSuccess()) {
                                        future.channel().close();
                                    }
                                }
                            });
                        }
                        byteBuf.release();
                    }
                });
            }
        }).remoteAddress(Init.Instance.getProperties().getProperty("proxyIP"), Integer.parseInt(Init.Instance.getProperties().getProperty("messagePort")));
        ChannelFuture channelFuture = b.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("0001");
                } else {
                    System.out.println("连接失败");
                }
            }
        });
        channelFuture.channel().closeFuture().addListener(future -> {
            System.out.println("channel传递连接 已断开  " + future.isSuccess());
        });
    }
}
