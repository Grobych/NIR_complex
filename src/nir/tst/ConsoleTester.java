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
import nir.model.algorithms.antmodel.AntRouting;
import nir.model.algorithms.beemodel.BeeRouting;
import nir.model.algorithms.swarmmodel.SwarmRouting;
import nir.model.base.Cargo;
import nir.model.base.Robot;
import nir.model.base.Route;
import nir.model.global.GlobalVariables;
import nir.model.global.VariablesLoader;
import nir.model.list.CargoList;
import nir.model.list.ObstacleList;
import nir.model.map.MapHolder;
import nir.model.util.CoordinateUtil;
import nir.model.util.RouteUtil;
import nir.model.util.logging.Log;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ConsoleTester {

    public void init(){
        Log.debug("init");
        Log.info("Creating Obstacles...");
        ObstacleList.init();
        Log.info("done.");
        Log.info("Map init...");
        MapHolder.INSTANCE.init();
        Log.info("done.");
    }

    private void testInit(){

    }

    public void testAll(int iterations){
        String s = new String("Test all types for "+iterations+ " iterations");
        Coordinate start = new Coordinate(200,250);
        Coordinate end = new Coordinate(850,670);
        List<Double> ant = new ArrayList<>();
        List<Double> swarm = new ArrayList<>();
        List<Double> bee = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            ant.add(testRoute(AlgorithmType.ANT,start,end));
            swarm.add(testRoute(AlgorithmType.PART,start,end));
            bee.add(testRoute(AlgorithmType.BEE,start,end));
        }

        showChart(s,ant,swarm,bee);
    }

    public Double testRoute(AlgorithmType type, Coordinate start, Coordinate end){
        try {
            Route route = getRouteTo(start,end,type);
            return RouteUtil.calculateRouteLenght(route);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Route getRouteTo(Coordinate start, Coordinate goal, AlgorithmType type) throws ExecutionException, InterruptedException {
        switch (type){
            case ANT: {
                AntRouting routing = new AntRouting(start,goal, VariablesLoader.get(GlobalVariables.getInstance().antParams));
                FutureTask<Route> future = new FutureTask<>(routing);
                new Thread(future).start();
                Route route = future.get();
                return route;
            }
            case PART: {
                SwarmRouting routing = new SwarmRouting(start,goal,VariablesLoader.get(GlobalVariables.getInstance().swarmParams)); // TODO: add params
                FutureTask<Route> future = new FutureTask<>(routing);
                new Thread(future).start();
                Route route = future.get();
                return route;
            }
            case BEE: {
                BeeRouting routing = new BeeRouting(start,goal,VariablesLoader.get(GlobalVariables.getInstance().beeParams));
                FutureTask<Route> future = new FutureTask<>(routing);
                new Thread(future).start();
                Route route = future.get();
                return route;
            }
            default: {
                Log.error("No such type!");
                return null;
            }
        }

    }

    public void testParam(AlgorithmType type, String param, double from, double to, double step){
        String s = new String("Test "+ type + " type with " + param + " as param:  "+ from + " - " +to + " step:"+ step);
        Log.info("Test "+ type + " type with " + param + " as param:  "+ from + " - " +to + " step:"+ step);
        List<Long> res = new ArrayList<>();
        List<Double> keys = new ArrayList<>();
        String params[];
        switch (type){
            case BEE: params = GlobalVariables.getInstance().beeParams; break;
            case PART: params = GlobalVariables.getInstance().swarmParams; break;
            case ANT: params = GlobalVariables.getInstance().antParams; break;
            default: break;
        }
        for (double p = from; p <= to; p+= step){
            keys.add(p);
            GlobalVariables.getInstance().editParam(param,p);
            Long l = testTime(type,
                    10,
                    GlobalVariables.getInstance().get("numberRobots").intValue(),
                    GlobalVariables.getInstance().get("robotCapacity").intValue());
            res.add(l);
        }
        showChart(s,keys,res);
    }


    private long testTime(AlgorithmType type, int numberCargo, int numberRobots, int robotCapacity){
        Long res = new Long(0);
        try {
            Tester t1 = new Tester();
            t1.setParam(numberCargo,numberRobots,robotCapacity);
            t1.init();
            t1.testInit(type);
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

            t1.init(list);
            t1.testInit(AlgorithmType.BEE);
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

//    private void tst1(){
//        Log.info("Test for 1 robot; 1 to 10 capacity...");
//        List<Long> list1 = new ArrayList<>();
//        for (int i = 1; i < 11; i++) {
//            Log.info("Starting for " + i + "capacity");
//            Long res = testTime(10,1,i);
//            Log.info("Done");
//            list1.add(res);
//        }
//        Log.info("All:");
//        for (Long v : list1) {
//            Log.info(v);
//        }
//        showChart("1 robot; 1 to 10 capacity", list1);
//    }
//    private void tst2(){
//        Log.info("Test for 1 capacity; 1 to 10 robot...");
//        List<Long> list = new ArrayList<>();
//        for (int i = 1; i < 11; i++) {
//            Log.info("Starting for " + i + "robots");
//            Long res = testTime(10,i,1,10,5);
//            Log.info("Done");
//            list.add(res);
//        }
//        Log.info("All:");
//        for (Long v : list) {
//            Log.info(v);
//        }
//
//        showChart("1 capacity; 1 to 10 robots", list);
//    }
//    private void tst3(){
//        Log.info("Test for 5 robot; 2 capacity, 1 to 5 agent...");
//        List<Long> list = new ArrayList<>();
//        for (int i = 1; i < 6; i++) {
//            Log.info("Starting for " + i + "agent");
//            Long res = testTime(10,5,2,10,i);
//            Log.info("Done");
//            list.add(res);
//        }
//        Log.info("All:");
//        for (Long v : list) {
//            Log.info(v);
//        }
//        showChart("5 robot; 2 capacity, 1 to 5 agent", list);
//    }
//    private void tst4(){
//        Log.info("Test for 2 capacity; 5 robots, 5 agents, 2 to 20 iterations...");
//        List<Long> list = new ArrayList<>();
//        for (int i = 2; i < 21; i+=2) {
//            Log.info("Starting for " + i + "iterations");
//            Long res = testTime(10,5,2,i,5);
//            Log.info("Done");
//            list.add(res);
//        }
//        Log.info("All:");
//        for (Long v : list) {
//            Log.info(v);
//        }
//        showChart("2 capacity; 5 robots, 5 agents, 2 to 20 iterations", list);
//    }
//    public void testTime(){
//        init();
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("1 - Fix time testTime; 2 - fix robot testTime.");
//        int s = scanner.nextInt();
//        switch (s) {
//            case 1:{
//                System.out.println("Fix time");
//                System.out.println("25 Cargoes.");
//                System.out.println("Maximum of robots (default 25) :");
//                int robotNumber = scanner.nextInt();
//                System.out.println("Time limit in sec (default 30):");
//                int timeLimit = scanner.nextInt() * 1000;
//                testFixTime(timeLimit,robotNumber); // time - 30 sec
//                break;
//            }
//            case 2:{
//                System.out.println("Fix robot");
//                System.out.println("25 Cargoes");
//                System.out.println("Number of tries: ");
//                int number = scanner.nextInt();
//                System.out.println("Number of robots:");
//                int robotNumber = scanner.nextInt();
//                testFixRobot(number,robotNumber);
//                break;
//            }
//            default:
//                System.out.println("some error");
//        }
//
//
////        tst1();
////        tst2();
////        tst3();
////        tst4();
//
//
//    }

//    public void testFixTime(long time, int maxRobots){
//        Log.info("Test for " + time/1000 + "s...");
//        Log.info("max robots: " + maxRobots);
//
//        List<Long> list = new ArrayList<>();
//        for (int i = 1; i < maxRobots + 1; i++) {
//            Log.info("Starting for " + i + "robots...");
//            Long res = testTime(25,i,1);
//            list.add(res);
//            Log.info("Done");
//            Log.info("Result: " + res/1000 + "  Limit time " + time/1000);
//            if (res < time){
//                Log.info("Robots number: "+ i);
//                showChart("25 cargoes, " + i + " robots" , list);
//                break;
//            }
//        }
//        Log.info("Max number of robots reached:");
//        for (Long v : list) {
//            Log.info(v);
//        }
//        showChart("25 cargoes, 25 robots", list);
//    }
//    public void testFixRobot(int tries ,int numberRobots){
//        Log.info("Test for fixed number of robots...");
//        Log.info("Number robots: " + numberRobots);
//        Log.info("Number tries: " + tries);
//        Long min = Long.MAX_VALUE;
//        int best = 0;
//        List<Long> list = new ArrayList<>();
//        List<List<Robot>> robotList = new ArrayList<>();
//        for (int i = 0; i < tries; i++) {
//            Log.info(i + " try...");
//            List<Robot> listRobot = generateRandRobots(numberRobots);
//            robotList.add(listRobot);
//            Long res = testRobot(25,listRobot,1,10,5);
//            if (res < min) {
//                best = i;
//                min = res;
//            }
//            list.add(res);
//            Log.info("Done");
//            Log.info("Result: " + res/1000);
//        }
//        Log.info("All tries done:");
//        for (Long v : list) {
//            Log.info(v);
//        }
//        showChart("25 cargoes," + numberRobots + " robots", list);
//        showMap(robotList.get(best));
//        for (Robot r : robotList.get(best)) {
//            System.out.println(r.getPosition());
//        }
//
//
//    }



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
    private void showChart(String s, List<Double> keys, List<Long> values) {
        Platform.runLater(() ->{
            Stage stage = new Stage();
            stage.setTitle("JavaFX Chart (Series)");

            NumberAxis x = new NumberAxis();
            NumberAxis y = new NumberAxis();

            LineChart<Number, Number> numberLineChart = new LineChart<Number, Number>(x,y);
            numberLineChart.setTitle(s);
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("time");
            ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
            ObservableList<XYChart.Data> datas2 = FXCollections.observableArrayList();
            for(int i=0; i<keys.size(); i++){
                datas.add(new XYChart.Data(keys.get(i),values.get(i)));
            }

            series1.setData(datas);

            Scene scene = new Scene(numberLineChart, 600,600);
            numberLineChart.getData().add(series1);
            stage.setScene(scene);

            stage.show();
        });
    }

    private void showChart(String s, List<Double> ant, List<Double> swarm, List<Double> bee) {
        Platform.runLater(() ->{
            Stage stage = new Stage();
            stage.setTitle("JavaFX Chart (Series)");

            NumberAxis x = new NumberAxis();
            NumberAxis y = new NumberAxis();

            LineChart<Number, Number> numberLineChart = new LineChart<Number, Number>(x,y);
            numberLineChart.setTitle(s);
            XYChart.Series series1 = new XYChart.Series();
            XYChart.Series series2 = new XYChart.Series();
            XYChart.Series series3 = new XYChart.Series();
            series1.setName("ant time");
            series2.setName("swarm time");
            series3.setName("bee time");
            ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
            ObservableList<XYChart.Data> datas2 = FXCollections.observableArrayList();
            ObservableList<XYChart.Data> datas3 = FXCollections.observableArrayList();
            for(int i=0; i<ant.size(); i++){
                datas.add(new XYChart.Data(i,ant.get(i)));
                datas2.add(new XYChart.Data(i,swarm.get(i)));
                datas3.add(new XYChart.Data(i,bee.get(i)));
            }

            series1.setData(datas);
            series2.setData(datas2);
            series3.setData(datas3);



            Scene scene = new Scene(numberLineChart, 600,600);
            numberLineChart.getData().add(series1);
            numberLineChart.getData().add(series2);
            numberLineChart.getData().add(series3);
            stage.setScene(scene);

            stage.show();
        });
    }


}
