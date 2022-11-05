package handler;

import io.netty.channel.*;

public class RProxyClientChannelInit extends ChannelInitializer {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new RProxyClientChannelHandler());
    }
}
