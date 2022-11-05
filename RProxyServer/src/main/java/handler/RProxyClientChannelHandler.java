package handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.AttributeKey;

import java.util.concurrent.TimeUnit;

import static init.Init.*;

public class RProxyClientChannelHandler extends ChannelInboundHandlerAdapter {
    private Channel serverChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (channelM != null) {
            clientChannels.add(ctx.channel());
            channelM.writeAndFlush(Unpooled.copiedBuffer(channels)).addListener(future -> {
                if (!future.isSuccess()) {
                    Channel channel = ctx.channel();
                    clientChannels.remove(channel);
                }
            });
            ctx.channel().eventLoop().schedule(() -> {
                if (!ctx.channel().config().isAutoRead()) {
                    channelM.writeAndFlush(Unpooled.copiedBuffer(channels)).addListener(future -> {
                        if (!future.isSuccess()) {
                            System.out.println("不该失败");
                            Channel channel = ctx.channel();
                            channel.close();
                            clientChannels.remove(channel);
                        }
                        System.out.println("有用");
                    });
                }

            }, 2, TimeUnit.SECONDS);
        } else {
            ctx.channel().close();
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (serverChannel == null) {
            Channel clientChannel = ctx.channel();
            serverChannel = (Channel) clientChannel.attr(AttributeKey.valueOf(clientChannel.id().asLongText())).get();
            //            Channel poll = serverChannels.poll();
//            if (poll != null) {
//                serverChannel = poll;
//            } else {
//                ctx.channel().close();
//                System.out.println("不该为null");
//            }

        }
        if (serverChannel != null) {
            serverChannel.writeAndFlush(msg);
        } else {
            ByteBuf byteBuf = (ByteBuf) msg;
            byteBuf.release();
            System.out.println("不该为null AAAA");
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
        ctx.close();
    }
}
