package handler;

import io.netty.channel.*;
import io.netty.util.AttributeKey;

import static init.Init.clientChannels;

public class RProxyServerChannelHandler extends ChannelInboundHandlerAdapter {
    private Channel clientChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        clientChannel = clientChannels.poll();
        if (clientChannel != null && clientChannel.isActive()) {
            clientChannel.attr(AttributeKey.valueOf(clientChannel.id().asLongText())).set(ctx.channel());
            clientChannel.config().setAutoRead(true);
        } else {
            ctx.channel().close();
            System.out.println("不该为null3" + clientChannel);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        clientChannel.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (clientChannel != null) {
            clientChannel.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.channel().close();
    }
}
