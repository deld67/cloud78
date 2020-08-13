package NettyClient;

import command.Command;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import property.Property;

import java.io.IOException;
import java.net.Socket;

public class NettyNetwork {
    private final Socket socket;
    private final ObjectDecoderInputStream is;
    private final ObjectEncoderOutputStream os;

    public NettyNetwork() throws IOException {
        this.socket = new Socket( Property.getServerHost(), Property.getServerPort());;
        this.os = new ObjectEncoderOutputStream( socket.getOutputStream() );
        this.is = new ObjectDecoderInputStream( socket.getInputStream() );
    }

    public void sendMessage(Command command) throws IOException {
        os.writeObject( command );
    }

    public Command readMessage() throws IOException, ClassNotFoundException {
        Command command = new Command();
        command = (Command) is.readObject();
        return command;
    }
}
