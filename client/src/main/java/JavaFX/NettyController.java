package JavaFX;

import NettyClient.NettyNetwork;
import command.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
    public ProgressBar progressBar;
    public ProgressIndicator progressIndicator;
    public DialogPane authDialog;
    public Label adLoginText;
    public Label adPassText;
    public PasswordField adPass;
    public TextField adLogin;
    public Button adLoginButton;
    public Label errMessg;
    private NettyNetwork network;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAuthDialog(false);
        errMessg.setText( "" );



    }

    private void showAuthDialog(boolean b) {
        authDialog.setVisible( b );
        adLoginText.setVisible( b );
        adPassText.setVisible( b );
        adPass.setVisible( b );
        adLogin.setVisible( b );
        adLoginButton.setVisible( b );
        errMessg.setVisible( b );
    }


    @SuppressWarnings("SingleStatementInBlock")
    public void connectDisconnect(ActionEvent actionEvent) throws IOException {
        //progressBar.setProgress( 0.5 );
        //progressIndicator.setProgress( 0.5 );

        if (connDisconn.getText().equals( "connect" )){
            showAuthDialog(true);
            try {
                network = new NettyNetwork();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connDisconn.setText( "disconnect" );

        }else {
            Command command = new Command();
            command.setType( CommandType.END );
            network.sendMessage(command);
            serverPath.setText( "" );
            serverView.getItems().clear();
            localView.getItems().clear();
            localPath.setText( "" );
            connDisconn.setText( "connect" );
            showAuthDialog(false);
        }
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


            if (!path.normalize().toString().equalsIgnoreCase( Property.getServerRootPath() )
                    && !path.toFile().isFile()
            ) {
                serverPath.setText( path.normalize().toString() );
                refreshServerView();
            }
        }
    }



    public void copyFromServer(ActionEvent actionEvent) throws IOException, ClassNotFoundException, InterruptedException {
        //получить файл с сервера
        Path pathFrom = Paths.get( serverPath.getText(), serverView.getSelectionModel().getSelectedItem() );
        Path pathTo = Paths.get( localPath.getText(), serverView.getSelectionModel().getSelectedItem() );
        System.out.println("get File:"+pathFrom.normalize().toString()+"ToServer:"+pathTo);
        Command command = new Command();
        command.setType( CommandType.GET_FILE );
        command.setData( new GetFileCommand(  pathFrom.toFile().getName(), pathFrom.toFile().getPath() ) );
        network.sendMessage(command);


        int pats = 2;
        int pat = 0;

        progressBar.setVisible( true );
        progressIndicator.setVisible( true );


        while (pats - 1 > pat) {
            command = network.readMessage();
            GetFileCommand getFileCommand = (GetFileCommand) command.getData();
            pats = getFileCommand.getPaths();
            pat = getFileCommand.getPath();
            System.out.println( "writeFile pat "+pat+" from "+pats );
            Utils.writeFile( pathTo.toFile().getPath(), getFileCommand.getBytes(), getFileCommand.getPath() );
            progressBar.setProgress( (double) (pat + 1) / pats );
            progressIndicator.setProgress( (double) (pat+1)/pats );
            System.out.println(progressBar.getProgress());
            Thread.sleep(10);
        }
        System.out.println( "refresh" );
        refreshServerView();
        refreshLocalView();
        progressBar.setVisible( false );
        progressIndicator.setVisible( false );

    }

    public void copyToServer(ActionEvent actionEvent) throws IOException, ClassNotFoundException, InterruptedException {
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
            System.out.println("Send command PUT_FILE to server at "+i+" by "+paths);
            network.sendMessage( command );

            //command = network.readMessage();
        }

        refreshServerView();
    }

    public void SendLogin(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        System.out.println("Login:"+adLogin.getText());
        System.out.println("Pass:"+adPass.getText());
        errMessg.setText( "" );
        Command command = new Command();
        command.setType( CommandType.AUTH );
        command.setData( new AuthCommand(adLogin.getText(), adPass.getText() ) );
        network.sendMessage( command );

        command = network.readMessage();
        if (command.getType() != CommandType.AUTH){
            errMessg.setText( "Ошибка авторизации. Попробуйте позже." );
        }else {
            AuthCommand authCommand = (AuthCommand) command.getData();
            if (!authCommand.getResult().equalsIgnoreCase( "OK" )){
                errMessg.setText(  authCommand.getResult());
            } else{
                localPath.setText( Property.getClientsRootPath() );
                serverPath.setText( Property.getServerRootPath() );

                refreshLocalView();
                refreshServerView();
                showAuthDialog(false);
                adLogin.setText( "" );
                adPass.setText( "" );
            }
        }
    }
}
