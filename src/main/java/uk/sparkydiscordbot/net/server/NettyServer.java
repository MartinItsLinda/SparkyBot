package uk.sparkydiscordbot.net.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.utils.Objects;
import uk.sparkydiscordbot.net.listener.ChannelInboundListener;

public class NettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private final int port;

    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private boolean running;

    /**
     * Construct a new {@link NettyServer}
     *
     * @param port The port to listen on
     */
    public NettyServer(final int port) {
        this.port = port;
    }

    /**
     * Start the server
     */
    public synchronized void start() {
        Objects.checkState(!this.running, "Server already running");

        LOGGER.info(String.format("Starting server on port %s...", this.port));

        this.running = true;

        this.boss = new NioEventLoopGroup();
        this.worker = new NioEventLoopGroup();

        try {

            final ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(this.boss, this.worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInboundListener())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            final ChannelFuture future = bootstrap.bind(this.port).sync();

            future.channel().closeFuture().sync();

        } catch (final InterruptedException ignored) {
            LOGGER.info("Server got interrupted during execution");
        } finally {
            if (this.running) {
                this.stop();
            }
        }

    }

    /**
     * Shutdown the server
     */
    public synchronized void stop() {
        if (this.running) {
            LOGGER.info("Server shutting down...");
            this.worker.shutdownGracefully();
            this.boss.shutdownGracefully();
        }
    }

}
