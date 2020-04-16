package uk.sparkydiscordbot.net.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelInboundListener extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelInboundListener.class);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        LOGGER.info(String.format("Connected: %s", ctx.channel().remoteAddress()));
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOGGER.info(String.format("Disconnected: %s (Channel Inactive)", ctx.channel().remoteAddress()));
    }

    @Override
    protected void messageReceived(final ChannelHandlerContext ctx, final String json) {

    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (cause.getMessage().contains("closed by remote host")) {
            return;
        }
        cause.printStackTrace();
    }

}
