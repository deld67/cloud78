import JavaFX.NettyController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;



public class IOClient extends Application {
    NettyController controller;
    Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println("Client start");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.progressBar.setVisible( false );
        controller.progressIndicator.setVisible( false );

        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();


    }


    @Override
    public void stop() throws Exception {


    }

    public static void main(String[] args) {

        launch(args);

    }
}
