package JavaFx;

import Netty.NettyServer;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public Button serverId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void startServer(ActionEvent actionEvent) {

        if (serverId.getText().equals( "Запустить сервер" )){
            new Thread(new NettyServer()).start();
            serverId.setText( "Остановить сервер" );
        }else { serverId.setText( "Запустить сервер" );}

    }
}
