import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                int channelHash = byteBuf.readInt();
                                if (byteBuf.readableBytes() != 0) {
                                    System.out.println("======================");
                                }
                                Bootstrap b = new Bootstrap();
                                b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<>() {
                                    @Override
                                    protected void initChannel(Channel ch) throws Exception {
                                        ch.pipeline().addLast(new TargetServerHandler(group));
                                    }
                                });
                                b.connect(new InetSocketAddress(Init.Instance.getProperties().getProperty("ip"), Integer.parseInt(Init.Instance.getProperties().getProperty("port")))).addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture future) throws Exception {
                                        if (future.isSuccess()) {
                                            future.channel().write(Unpooled.copiedBuffer("11111".getBytes(StandardCharsets.UTF_8)));
                                            future.channel().writeAndFlush(Unpooled.copyInt(channelHash));
                                        }
                                    }
                                });
                                byteBuf.release();
                            }
                        });
                    }
                });
        ChannelFuture channelFuture = b.connect(Init.Instance.getProperties().getProperty("ip"), Integer.parseInt(Init.Instance.getProperties().getProperty("port"))).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("0001");
                    future.channel().writeAndFlush(Unpooled.copiedBuffer("00000".getBytes()));
                }
            }
        });
        ChannelId id = channelFuture.channel().id();
        String s = id.asLongText();
        String s1 = id.asShortText();
        System.out.println(s.getBytes().length);
        System.out.println(s.length());
    }
}
