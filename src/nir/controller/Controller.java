package nir.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import nir.algorythms.swarmmodel.SwampRouting;
import nir.list.ObstacleList;
import nir.list.RobotList;
import nir.model.Robot;
import nir.model.global.GlobalVariables;
import nir.model.global.VariablesFileLoader;
import nir.model.map.MapHolder;
import nir.threads.RenderThread;
import nir.tst.TestType;
import nir.tst.Tester;
import nir.util.logging.Log;
import org.locationtech.jts.geom.Coordinate;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class Controller implements Initializable, PropertyChangeListener {
    private RenderThread renderThread;
    private Thread t1,t2;
    private Tester tester = new Tester();
    @FXML
    TextArea logArea;
    @FXML
    Button initButton, routeButton, startButton, partButton;
    public Controller getInstance(){
        return this;
    }
    @FXML
    Canvas mapCanvas, robotCanvas, utilCanvas;


    public static boolean initReady, routingReady;


    @FXML
    public void getRouteButtonClick(){

    }
    @FXML
    public void partButtonClick(){
        SwampRouting swampRouting = new SwampRouting(new Coordinate(200,600), new Coordinate(700,200));
        Thread thread = new Thread(swampRouting);
        thread.start();
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

        Log.info("Map init...");
        MapHolder.INSTANCE.init();
        Log.info("done.");

        //GoalList.goalList.add(new Goal(500,500));

        renderThread = new RenderThread();
        renderThread.setCanvases(mapCanvas, robotCanvas, utilCanvas);
        renderThread.start();

        initButton.setDisable(false);

//        VariablesLoader.load();
    }


    public void stop() {
        VariablesFileLoader.save();
        if (renderThread != null) renderThread.stopThread();
        Platform.exit();
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        logArea.appendText(propertyChangeEvent.getNewValue().toString());
    }
}
