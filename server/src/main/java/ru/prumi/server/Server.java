package ru.prumi.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.bobahe.gbcloud.common.fs.FSUtils;
import ru.prumi.server.db.SQLHandler;
import ru.prumi.server.net.handlers.ServerChannelInitializer;
import ru.prumi.server.properties.ApplicationProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class Server {
    private int port = Integer.parseInt(ApplicationProperties.getInstance().getProperty("port"));


    Server() throws IOException {
        if (!checkRootFolder()) {
            throw new IllegalStateException("Отстутствует папка-хранилище!");
        }

        if (!SQLHandler.connect()) {
            throw new RuntimeException("Не удалось подключиться к БД.");
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //.handler(new LoggingHandler(LogLevel.WARN))
                    .childHandler(new ServerChannelInitializer());

            b.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private boolean checkRootFolder() throws IOException {
        Files.createDirectories(Paths.get(ApplicationProperties.getInstance().getProperty("root.directory")));
        return FSUtils.checkFolders(
                Paths.get(ApplicationProperties.getInstance().getProperty("root.directory"))
        );
    }
}
