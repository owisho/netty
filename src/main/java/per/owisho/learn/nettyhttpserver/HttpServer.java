package per.owisho.learn.nettyhttpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServer {

    private final int port;

    public HttpServer(int port){
        this.port = port;
    }

    public static void main(String[] args) {
        if(args.length!=1){
            System.err.println("Usage："+HttpServer.class.getSimpleName()+" <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new HttpServer(port).start();
    }

    private void start() {

        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("initChannel ch："+ch);
                        ch.pipeline()
                                .addLast("decoder",new HttpRequestDecoder())//用于解码request
                                .addLast("encoder",new HttpResponseEncoder())//用于编码response
                                .addLast("aggregator",new HttpObjectAggregator(512*1024))//消息聚合器（重要）
                                .addLast("handler",new HttpHandler());//自己的处理接口
                    }
                })
                .option(ChannelOption.SO_BACKLOG,128)//determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE,Boolean.TRUE)
                .bind(this.port);

    }

}
