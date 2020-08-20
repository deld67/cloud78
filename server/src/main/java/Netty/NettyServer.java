package Netty;

import Netty.Auth.AuthService;
import Netty.Auth.BaseAuthService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import property.Property;

public class NettyServer implements Runnable{
    EventLoopGroup auth = new NioEventLoopGroup(1);
    EventLoopGroup worker = new NioEventLoopGroup();
    private AuthService authService;


    @Override
    public void run() {


        try {

            this.authService = new BaseAuthService();
            //this.authService = new PostgreSQLAuthService();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth,worker)
                    .channel( NioServerSocketChannel.class )
                    .childHandler( new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder( ClassResolvers.cacheDisabled( null )),
                                    new ObjectEncoder(),
                                    new ObjectReadHandler(authService)
                                    );

                        }
                    } );
            ChannelFuture future = bootstrap.bind( Property.getServerHost(), Property.getServerPort() ).sync();
            System.out.println("Netty server started");
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            authService.stop();
            stopServer();
        }
    }

    public void stopServer(){
        auth.shutdownGracefully();
        worker.shutdownGracefully();
    }

    public static void main(String[] args) {
        new Thread(new NettyServer()).start();
    }

}
