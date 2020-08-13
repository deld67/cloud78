package JavaFX;

import NettyClient.NettyNetwork;
import command.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.input.MouseEvent;
import property.Property;

import utils.Utils;


public class NettyController implements Initializable {
    public ListView<String> localView;
    public ListView<String> serverView;
    public TextField serverPath;
    public TextField localPath;
    public Button connDisconn;
    public TextField text;
    private NettyNetwork network;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }



    @SuppressWarnings("SingleStatementInBlock")
    public void connectDisconnect(ActionEvent actionEvent) {
        if (connDisconn.getText().equals( "connect" )){
            try {
                network = new NettyNetwork();

                localPath.setText( Property.getClientsRootPath() );
                serverPath.setText( Property.getServerRootPath() );

                refreshLocalView();
                refreshServerView();


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            connDisconn.setText( "disconnect" );

        }else { connDisconn.setText( "connect" );}
    }

    private void refreshServerView() throws IOException, ClassNotFoundException {
        Command command = new Command();
        command.setType( CommandType.GET_FILE_LIST );
        command.setData( new ListFilesCommand(serverPath.getText()) );
        network.sendMessage(command);
        command = network.readMessage();
        ListFilesCommand listFilesCommand = (ListFilesCommand) command.getData();
        serverView.getItems().clear();
        for (String c: listFilesCommand.getList()) {
            serverView.getItems().add( c );
        }
    }

    private void refreshLocalView(){
        localView.getItems().clear();
        List<String> locatFilesList = Utils.getFileList( localPath.getText() );
        for (String s: locatFilesList) {
            localView.getItems().add( s );
        }
    }

    public void mouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().name().equalsIgnoreCase( "PRIMARY" )
            && mouseEvent.getClickCount() == 2 ){
            Path path = Paths.get(localPath.getText(), localView.getSelectionModel().getSelectedItem());

            if (!path.normalize().toString().equalsIgnoreCase( Property.getClientsRootPath())
                && !path.toFile().isFile()
            ) {
                localPath.setText( path.normalize().toString() );
                System.out.println("new dir:"+localPath.getText());
                refreshLocalView();
            }
        }
    }

    public void mouseClickServer(MouseEvent mouseEvent) throws IOException, ClassNotFoundException {
        if (mouseEvent.getButton().name().equalsIgnoreCase( "PRIMARY" )
                && mouseEvent.getClickCount() == 2 ) {
            Path path = Paths.get( serverPath.getText(), serverView.getSelectionModel().getSelectedItem() );

            System.out.println(path.normalize().toString()+"!="+Property.getServerRootPath());
            System.out.println("path.toFile().isFile() "+path.toFile().isFile());

            if (!path.normalize().toString().equalsIgnoreCase( Property.getServerRootPath() )
                    && !path.toFile().isFile()
            ) {
                serverPath.setText( path.normalize().toString() );
                System.out.println( "new dir:" + serverPath.getText() );
                refreshServerView();
            }
        }
    }

    public void copyFromServer(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        //получить файл с сервера
        Path pathFrom = Paths.get( serverPath.getText(), serverView.getSelectionModel().getSelectedItem() );
        Path pathTo = Paths.get( localPath.getText(), serverView.getSelectionModel().getSelectedItem() );
        System.out.println("get File:"+pathFrom.normalize().toString()+"ToServer:"+pathTo);
        Command command = new Command();
        command.setType( CommandType.GET_FILE );
        //command.setData( new ListFilesCommand(serverPath.getText()) );
        command.setData( new GetFileCommand(  pathFrom.toFile().getName(), pathFrom.toFile().getPath() ) );
        System.out.println("sendCommand to server");
        network.sendMessage(command);
        System.out.println("readMessage from server");
        command = network.readMessage();
        GetFileCommand getFileCommand = (GetFileCommand) command.getData();
        System.out.println("writeFile");
        Utils.writeFile(pathTo.toFile().getPath(), getFileCommand.getBytes(), getFileCommand.getPath() );
       if (getFileCommand.getPaths() == getFileCommand.getPath()) {
           System.out.println( "refresh" );
           refreshServerView();
           refreshLocalView();
       }
    }

    public void copyToServer(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        int paths = 0;
        long filesize = 0;
        //передать файл на сервер
        Path pathFrom = Paths.get( localPath.getText(), localView.getSelectionModel().getSelectedItem() );
        Path pathTo = Paths.get( serverPath.getText(), localView.getSelectionModel().getSelectedItem() );
        filesize = pathFrom.toFile().length();
        paths = (int) filesize /Property.getBuferSize();
        if (paths * Property.getBuferSize() < filesize) paths++;

        System.out.println("put File:"+pathFrom.normalize().toString()+"ToServer:"+pathTo);
        Command command = new Command();
        command.setType( CommandType.PUT_FILE );
        PutFileCommand putFileCommand = new PutFileCommand( pathFrom.toFile().getName(), pathTo.toFile().getPath()  );
        for (int i = 0; i < paths; i++) {
            putFileCommand.setBites( Utils.readFile( pathFrom.toFile().getPath(), i, Property.getBuferSize() ) );
            putFileCommand.setPath( i );
            putFileCommand.setPaths( paths );
            command.setData( putFileCommand );
            network.sendMessage( command );

            command = network.readMessage();
        }

        refreshServerView();

    }
}
