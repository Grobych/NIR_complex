package nir.controller;

import com.sun.xml.internal.ws.Closeable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import nir.list.ObstacleList;
import nir.list.RobotList;
import nir.model.Robot;
import nir.model.global.GlobalVariables;
import nir.model.global.VariablesFileLoader;
import nir.tst.TestType;
import nir.tst.Tester;
import nir.util.logging.Log;

import javax.xml.ws.WebServiceException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class Controller implements Initializable, Closeable, PropertyChangeListener {

    private Thread t1,t2;
    private Tester tester = new Tester();
    @FXML
    TextArea logArea;
    @FXML
    Button initButton, routeButton, startButton, partButton;
    public Controller getInstance(){
        return this;
    }

    public static boolean initReady, routingReady;


    @FXML
    public void getRouteButtonClick(){

    }
    @FXML
    public void partButtonClick(){
//        SwampRouting swampRouting = new SwampRouting(new Coordinate(200,600), new Coordinate(700,200));
//        Thread thread = new Thread(swampRouting);
//        thread.start();
    }


    @FXML
    public void initButtonClick() throws ExecutionException, InterruptedException {
        Log.info("init");
        tester.setParam(10, GlobalVariables.getInstance().numberRobots,GlobalVariables.getInstance().robotCapasity);
        tester.init();
        Log.info("init done");
        routeButton.setDisable(false);

    }

    @FXML
    public void routeButtonClick() throws ExecutionException, InterruptedException {
        tester.testInit(TestType.BEE);
        startButton.setDisable(false);
        initButton.setDisable(true);
    }

    @FXML
    public void startClick(){
        Log.info("start");
        routeButton.setDisable(true);
        for (Robot robot : RobotList.getRobotList()) {
            System.out.println(robot.getRoute().list.size());
        }
        tester.run();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Log.debug("init");
        Log.info("Creating Obstacles...");
        ObstacleList.init();
        Log.info("done.");



        //GoalList.goalList.add(new Goal(500,500));



        initButton.setDisable(false);

//        VariablesLoader.load();
    }


    public void stop() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        logArea.appendText(propertyChangeEvent.getNewValue().toString());
    }

    @Override
    public void close() throws WebServiceException {
        VariablesFileLoader.save();

    }
}
