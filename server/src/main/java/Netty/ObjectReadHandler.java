package Netty;

import command.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import property.Property;
import utils.Utils;

public class ObjectReadHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client disconnected");
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server getting:"+msg.toString());
       if (msg instanceof Command){
           Command command = (Command) msg;
           switch (command.getType() ){
               case GET_FILE_LIST:
                   ListFilesCommand listFilesCommand = (ListFilesCommand) command.getData();
                   System.out.println(listFilesCommand.getFilePath() );
                   listFilesCommand.setList( Utils.getFileList( listFilesCommand.getFilePath() ) );
                   command.setData( listFilesCommand );
                   ctx.channel().writeAndFlush( command );
                   break;
               case GET_FILE:
                   GetFileCommand getFileCommand = (GetFileCommand) command.getData();
                   System.out.println(getFileCommand.getFilePath()+" "+getFileCommand.getFilename());
                   System.out.println("GET_FILE");
                   getFileCommand.setBites( Utils.readFile(getFileCommand.getFilePath(), 1, Property.getBuferSize() ) );
                   command.setData( getFileCommand );
                   System.out.println("answered");
                   ctx.channel().writeAndFlush( command );
                   break;
               case PUT_FILE:
                   PutFileCommand putFileCommand = (PutFileCommand) command.getData();
                   System.out.println(putFileCommand.getFilePath()+" "+putFileCommand.getFilename());
                   System.out.println("GET_FILE");
                   Utils.writeFile( putFileCommand.getFilePath(), putFileCommand.getBytes(), putFileCommand.getPath());
                   command.setType( CommandType.RESULT );
                   command.setData( new ResultCommand( "OK" ) );
                   ctx.channel().writeAndFlush( command );
                   break;
           }

       }else {
           System.out.println("other:"+msg.getClass().toString());
       }
    }
}
