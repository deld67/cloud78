package Netty;

import Netty.Auth.AuthService;
import command.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import property.Property;
import utils.Utils;

import java.nio.file.Paths;

public class ObjectReadHandler extends ChannelInboundHandlerAdapter {
    private AuthService authService;

    public ObjectReadHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        authService.start();
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
           System.out.println("command.getType() = "+command.getType() );
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
                   System.out.println("ORH GET_FILE");
                   int paths = 0;
                   long filesize = 0;
                   filesize = Paths.get(getFileCommand.getFilePath()).toFile().length();
                   paths = (int) filesize /Property.getBuferSize();
                   if (paths * Property.getBuferSize() < filesize) paths++;

                   for (int i = 0; i < paths; i++) {
                       getFileCommand.setPath( i );
                       getFileCommand.setPaths(paths);
                       getFileCommand.setBites( Utils.readFile(getFileCommand.getFilePath(), i, Property.getBuferSize() ) );
                       command.setData( getFileCommand );
                       System.out.println("read from File path "+i+ " of "+paths +" by len "+Property.getBuferSize());
                       ctx.channel().writeAndFlush( command );
                   }
                   break;
               case PUT_FILE:
                   PutFileCommand putFileCommand = (PutFileCommand) command.getData();
                   System.out.println(putFileCommand.getFilePath()+" "+putFileCommand.getFilename());
                   System.out.println("PUT_FILE");
                   Utils.writeFile( putFileCommand.getFilePath(), putFileCommand.getBytes(), putFileCommand.getPath());
                   break;
               case END:
                   ctx.channel().close();
               case AUTH:
                   AuthCommand authCommand = (AuthCommand) command.getData();
                   authCommand.setUsername( authService.getUsernameByLoginAndPassword( authCommand.getLogin(), authCommand.getPassword() ) );
                   System.out.println("return auth "+authCommand.getUsername());
                   if (authCommand.getUsername() == null){
                       authCommand.setResult( "Не верный логин или пароль" );
                   }else {
                       authCommand.setResult( "ok" );
                       authCommand.setUserFolder( authService .getUserFolderByUsername( authCommand.getUsername() ));

                   }

                   command.setData( authCommand );
                   ctx.channel().writeAndFlush( command );
           }

       }else {
           System.out.println("other:"+msg.getClass().toString());
       }
    }
}
