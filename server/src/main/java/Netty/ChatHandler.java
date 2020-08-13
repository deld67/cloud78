package Netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatHandler extends SimpleChannelInboundHandler<String>{
    private static ConcurrentLinkedDeque<SocketChannel> clients = new ConcurrentLinkedDeque<>();

    private  String name;
    private static int cnt = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected");
        cnt++;
        name = "user#"+cnt;
        clients.add( (SocketChannel) ctx.channel() );
        for (SocketChannel socket: clients) {
            if (!socket.equals( (SocketChannel) ctx.channel() )) {
                socket.writeAndFlush( "Client "+name+" connected" );
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client "+name+" disconnect");
        clients.remove((SocketChannel) ctx.channel());
        for (SocketChannel socket: clients) {
            socket.writeAndFlush( "Client "+name+" disconnect");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println( "message from client"+name+":" + s);
        for (SocketChannel socket: clients) {
            if (!socket.equals( (SocketChannel) ctx.channel() )) {
                socket.writeAndFlush( "["+name+"]:"+s );
            }
        }
    }


}
