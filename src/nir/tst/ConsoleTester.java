package nir.tst;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import nir.list.CargoList;
import nir.list.ObstacleList;
import nir.model.Robot;
import nir.model.global.GlobalVariables;
import nir.model.map.Cargo;
import nir.model.map.MapHolder;
import nir.util.CoordinateUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ConsoleTester {
    final static Logger logger = Logger.getLogger(ConsoleTester.class);

    private void init(){
        logger.debug("init");
        logger.info("Creating Obstacles...");
        ObstacleList.init();
        logger.info("done.");
        logger.info("Map init...");
        MapHolder.INSTANCE.init();
        logger.info("done.");
    }

    private void testInit(){

    }
    private long testTime(int numberCargo, int numberRobots, int robotCapacity, int iteration, int agents){
        Long res = new Long(0);
        try {
            Tester t1 = new Tester();
            t1.setParam(numberCargo,numberRobots,robotCapacity);
            GlobalVariables.getInstance().iterations = iteration;
            GlobalVariables.getInstance().agentsNumber = agents;
            t1.init();
            t1.testInit(TestType.BEE);
            FutureTask<Long> future = new FutureTask<>(t1);
            new Thread(future).start();
            res = future.get();
            System.out.println("Time: " + res);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    private long testRobot(int numberCargo, List<Robot> list, int robotCapacity, int iteration, int agents){
        Long res = new Long(0);
        try {
            Tester t1 = new Tester();
            t1.setParam(numberCargo,list.size(),robotCapacity);
            GlobalVariables.getInstance().iterations = iteration;
            GlobalVariables.getInstance().agentsNumber = agents;
            t1.init(list);
            t1.testInit(TestType.BEE);
            FutureTask<Long> future = new FutureTask<>(t1);
            new Thread(future).start();
            res = future.get();
            System.out.println("Time: " + res);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    private void tst1(){
        logger.info("Test for 1 robot; 1 to 10 capacity...");
        List<Long> list1 = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            logger.info("Starting for " + i + "capacity");
            Long res = testTime(10,1,i,10,5);
            logger.info("Done");
            list1.add(res);
        }
        logger.info("All:");
        for (Long v : list1) {
            logger.info(v);
        }
        showChart("1 robot; 1 to 10 capacity", list1);
    }
    private void tst2(){
        logger.info("Test for 1 capacity; 1 to 10 robot...");
        List<Long> list = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            logger.info("Starting for " + i + "robots");
            Long res = testTime(10,i,1,10,5);
            logger.info("Done");
            list.add(res);
        }
        logger.info("All:");
        for (Long v : list) {
            logger.info(v);
        }

        showChart("1 capacity; 1 to 10 robots", list);
    }
    private void tst3(){
        logger.info("Test for 5 robot; 2 capacity, 1 to 5 agent...");
        List<Long> list = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            logger.info("Starting for " + i + "agent");
            Long res = testTime(10,5,2,10,i);
            logger.info("Done");
            list.add(res);
        }
        logger.info("All:");
        for (Long v : list) {
            logger.info(v);
        }
        showChart("5 robot; 2 capacity, 1 to 5 agent", list);
    }
    private void tst4(){
        logger.info("Test for 2 capacity; 5 robots, 5 agents, 2 to 20 iterations...");
        List<Long> list = new ArrayList<>();
        for (int i = 2; i < 21; i+=2) {
            logger.info("Starting for " + i + "iterations");
            Long res = testTime(10,5,2,i,5);
            logger.info("Done");
            list.add(res);
        }
        logger.info("All:");
        for (Long v : list) {
            logger.info(v);
        }
        showChart("2 capacity; 5 robots, 5 agents, 2 to 20 iterations", list);
    }
    public void testTime(){
        init();
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - Fix time testTime; 2 - fix robot testTime.");
        int s = scanner.nextInt();
        switch (s) {
            case 1:{
                System.out.println("Fix time");
                System.out.println("25 Cargoes.");
                System.out.println("Maximum of robots (default 25) :");
                int robotNumber = scanner.nextInt();
                System.out.println("Time limit in sec (default 30):");
                int timeLimit = scanner.nextInt() * 1000;
                testFixTime(timeLimit,robotNumber); // time - 30 sec
                break;
            }
            case 2:{
                System.out.println("Fix robot");
                System.out.println("25 Cargoes");
                System.out.println("Number of tries: ");
                int number = scanner.nextInt();
                System.out.println("Number of robots:");
                int robotNumber = scanner.nextInt();
                testFixRobot(number,robotNumber);
                break;
            }
            default:
                System.out.println("some error");
        }


//        tst1();
//        tst2();
//        tst3();
//        tst4();


    }

    public void testFixTime(long time, int maxRobots){
        logger.info("Test for " + time/1000 + "s...");
        logger.info("max robots: " + maxRobots);

        List<Long> list = new ArrayList<>();
        for (int i = 1; i < maxRobots + 1; i++) {
            logger.info("Starting for " + i + "robots...");
            Long res = testTime(25,i,1,10,5);
            list.add(res);
            logger.info("Done");
            logger.info("Result: " + res/1000 + "  Limit time " + time/1000);
            if (res < time){
                logger.info("Robots number: "+ i);
                showChart("25 cargoes, " + i + " robots" , list);
                break;
            }
        }
        logger.info("Max number of robots reached:");
        for (Long v : list) {
            logger.info(v);
        }
        showChart("25 cargoes, 25 robots", list);
    }
    public void testFixRobot(int tries ,int numberRobots){
        logger.info("Test for fixed number of robots...");
        logger.info("Number robots: " + numberRobots);
        logger.info("Number tries: " + tries);
        Long min = Long.MAX_VALUE;
        int best = 0;
        List<Long> list = new ArrayList<>();
        List<List<Robot>> robotList = new ArrayList<>();
        for (int i = 0; i < tries; i++) {
            logger.info(i + " try...");
            List<Robot> listRobot = generateRandRobots(numberRobots);
            robotList.add(listRobot);
            Long res = testRobot(25,listRobot,1,10,5);
            if (res < min) {
                best = i;
                min = res;
            }
            list.add(res);
            logger.info("Done");
            logger.info("Result: " + res/1000);
        }
        logger.info("All tries done:");
        for (Long v : list) {
            logger.info(v);
        }
        showChart("25 cargoes," + numberRobots + " robots", list);
        showMap(robotList.get(best));
        for (Robot r : robotList.get(best)) {
            System.out.println(r.getPosition());
        }


    }



    private List<Robot> generateRandRobots(int numberRobots) {
        List<Robot> res = new ArrayList<>();
        for (int i = 0; i < numberRobots; i++) {
            Robot r = new Robot(CoordinateUtil.randCoordinate(0,100,900,800));
            res.add(r);
        }
        return res;
    }

    private void showMap(List<Robot> list) {
        Platform.runLater(() ->{

            int x = MapHolder.INSTANCE.getLevelMap().getxSize();
            int y = MapHolder.INSTANCE.getLevelMap().getySize();


            Stage stage = new Stage();
            stage.setTitle("JavaFX Map");
            Group root = new Group();
            Image robotImage = new Image("file:robot.jpg");
            Image levelMapImage = new Image("file:levelmap2.jpg");
            Image goalImage = new Image("file:resources\\star.png");
            Image cargoImage = new Image("file:resources\\Box.png");

            Canvas canvas = new Canvas(MapHolder.INSTANCE.getLevelMap().getxSize(),MapHolder.INSTANCE.getLevelMap().getySize());
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(levelMapImage, 0, 0, x, y);

            CargoList.generate();
            for (Cargo cargo : CargoList.cargoList) {
                if (!cargo.isTaken()) gc.drawImage(cargoImage, cargo.getX() - 16, cargo.getY() - 16, 32, 32);
                if (!cargo.isDelivered()) gc.drawImage(goalImage, cargo.getGoal().getX() - 15, cargo.getGoal().getY() - 15, 30, 30);
            }

            for (Robot robot : list) {
                gc.drawImage(robotImage, robot.getX(), robot.getY());
//                gc.save();
            }

            gc.save();
            root.getChildren().add(canvas);
            Scene scene = new Scene(root, x,y);


            stage.setScene(scene);

            stage.show();
        });
    }

    private void showChart(String s, List<Long> list) {
            Platform.runLater(() ->{
                Stage stage = new Stage();
//            try {
////                Parent root = FXMLLoader.load(getClass().getResource("view/ChartScene.fxml"));
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
                stage.setTitle("JavaFX Chart (Series)");

                NumberAxis x = new NumberAxis();
                NumberAxis y = new NumberAxis();

                LineChart<Number, Number> numberLineChart = new LineChart<Number, Number>(x,y);
                numberLineChart.setTitle(s);
                XYChart.Series series1 = new XYChart.Series();
                series1.setName("time");
                ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
                ObservableList<XYChart.Data> datas2 = FXCollections.observableArrayList();
                for(int i=0; i<list.size(); i++){
                    datas.add(new XYChart.Data(i,list.get(i)));
                }

                series1.setData(datas);

                Scene scene = new Scene(numberLineChart, 600,600);
                numberLineChart.getData().add(series1);
                stage.setScene(scene);

                stage.show();
            });

        }

}
