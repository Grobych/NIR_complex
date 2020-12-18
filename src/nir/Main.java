package nir;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nir.controller.Controller;
import nir.model.util.logging.LogString;
import org.apache.log4j.Logger;

public class Main extends Application {
    final static Logger logger = Logger.getLogger(Main.class);


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/mainScene.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        System.out.println(controller);
        primaryStage.setTitle("NIR");
        primaryStage.setScene(new Scene(root, 1500, 800));
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
        });
        primaryStage.show();

        LogString.addPropertyChangeListener(controller);
    }

    public static void main(String[] args) {
        launch(args);
//        ConsoleTester tester = new ConsoleTester();
//        tester.init();
//        tester.testParam(AlgorithmType.BEE,"beeNumber",1,20,1);

        System.exit(0);
    }
}
