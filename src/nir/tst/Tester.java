package nir.tst;

import nir.algorythms.antmodel.AntRouting;
import nir.algorythms.beemodel.BeeRouting;
import nir.algorythms.swarmmodel.SwampRouting;
import nir.list.CargoList;
import nir.list.RobotList;
import nir.model.Robot;
import nir.model.RobotCargo;
import nir.model.Route;
import nir.model.global.GlobalVariables;
import nir.model.map.Cargo;
import nir.threads.RobotMoveThread;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Tester implements Runnable, Callable<Long> {

    final static Logger logger = Logger.getLogger(Tester.class);

    private Map<Cargo, Boolean> cargoInRoute = new HashMap<>();
    private Map<Robot, Coordinate> points = new HashMap<>();

    private List<Robot> robotBackUp = new ArrayList<>();
    private List<Cargo> cargoBackUp = new ArrayList<>();

    private int numberCargo, numberRobots, robotCapacity;

    public void setParam(int numberCargo, int numberRobots, int robotCapacity){
        this.numberCargo = numberCargo;
        this.numberRobots = numberRobots;
        this.robotCapacity = robotCapacity;
    }

    public void testInit(TestType type) throws ExecutionException, InterruptedException {

        while (!isAllCargoInRoutes()){
            for (Robot robot : RobotList.getRobotList()) {
                Route res = new Route();
                List<Cargo> cargoList = new ArrayList<>();
                for (int i = 0; i < robotCapacity; i++) {
                    Cargo cargo = getNearCargo(points.get(robot));
                    if (cargo == null) break;
                    cargoList.add(cargo);
                    res.addRoute(getRouteTo(points.get(robot),cargo.getPosition(), type));
                    inRoute(cargo);
                    points.put(robot, cargo.getPosition());
                }
                for (Cargo c : cargoList) {
                    res.addRoute(getRouteTo(points.get(robot), c.getGoal().getPosition(), type));
                    points.put(robot,c.getGoal().getPosition());
                }
                addRoute(robot,res);
            }
        }
        logger.info("Routing is done");
    }

    private void clean() {
        CargoList.cargoList = cargoBackUp;
        RobotList.clear();
        generateRobots(numberRobots);
        for (Cargo cargo : cargoInRoute.keySet()) {
            cargoInRoute.put(cargo,Boolean.FALSE);
        }
        points.clear();
        for (Robot robot : RobotList.getRobotList()) {
            points.put(robot,robot.getPosition());
        }
    }

    public void test() throws ExecutionException, InterruptedException {
        long time = 0, before, after;
        before = System.currentTimeMillis();
        runRobots();
        after = System.currentTimeMillis();
        time = after - before;
        logger.info("Time for all: " + time);
    }

    private void runRobots() {
        List<Thread> list = new ArrayList<>();
        for (Robot robot : RobotList.getRobotList()) {
            RobotMoveThread move = new RobotMoveThread();
            move.setRobot(robot);
            move.setRoute(robot.getRoute());
            Thread thread = new Thread(move);
            list.add(thread);
//            thread.start();
        }
        for (Thread thread : list) {
            thread.start();
        }
        for (Thread thread : list) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private Route getRouteTo(Coordinate start, Coordinate goal, TestType type) throws ExecutionException, InterruptedException {
        switch (type){
            case ANT: {
                AntRouting routing = new AntRouting(
                        start,
                        goal,
                        GlobalVariables.getInstance().iterations,
                        GlobalVariables.getInstance().agentsNumber,
                        GlobalVariables.getInstance().phInit);
                routing.setParams(
                        GlobalVariables.getInstance().goal,
                        GlobalVariables.getInstance().ph,
                        GlobalVariables.getInstance().rand);

                FutureTask<Route> future = new FutureTask<>(routing);
                new Thread(future).start();
                Route route = future.get();
                return route;
            }
            case PART: {
                SwampRouting routing = new SwampRouting(start,goal); // TODO: add params
                FutureTask<Route> future = new FutureTask<>(routing);
                new Thread(future).start();
                Route route = future.get();
                return route;
            }
            case BEE: {
                BeeRouting routing = new BeeRouting(5,5,5,start,goal);
                FutureTask<Route> future = new FutureTask<>(routing);
                new Thread(future).start();
                Route route = future.get();
                return route;
            }
            default: {
                logger.error("No such type!");
                return null;
            }
        }

    }

    private void addRoute(Robot robot, Route route) {
        Route r = robot.getRoute();
        r.addRoute(route); //TODO: nullpointer
        robot.setRoute(r);
    }

    private void inRoute(Cargo cargo) {
        cargoInRoute.put(cargo,true);
    }

    private boolean isAllCargoInRoutes() {
        Set<Cargo> cargoSet = cargoInRoute.keySet();
        for (Cargo cargo : cargoSet) {
            if (cargoInRoute.get(cargo) == false) return false;
        }
        return true;
    }

    private synchronized void generateRobots(int numberRobots) {
        RobotList.clear();
        robotBackUp.clear();
        for (int i = 0; i < numberRobots; i++) {
            Robot robot = new RobotCargo(new Coordinate(200,600),robotCapacity);
            points.put(robot,robot.getPosition());
            RobotList.add(robot);
            robotBackUp.add(robot);
        }
    }


    private synchronized void generateCargo(int numberCargo) {
        CargoList.cargoList.clear();
        cargoBackUp.clear();

        CargoList.generate();

        for (Cargo c : CargoList.cargoList) {
            cargoInRoute.put(c,Boolean.FALSE);
        }


//        for (int i = 0; i < numberCargo; i++) {
//            Cargo cargo = new Cargo(randCoordinate(0,100,1000,600), randCoordinate(0,0,1000,100));
//            CargoList.cargoList.add(cargo);
//            cargoInRoute.put(cargo,Boolean.FALSE);
//            cargoBackUp.add(cargo);
//        }
    }

    private Cargo getNearCargo(Coordinate coordinate) {
        Cargo res = null;
        double l = Double.MAX_VALUE;
        for (Cargo cargo : CargoList.cargoList) {
            if (!cargoInRoute.get(cargo))
                if (coordinate.distance(cargo.getPosition()) < l){
                    res = cargo;
                    l = coordinate.distance(res.getPosition());
                }
        }
        return res;
    }

    public void init(){
        generateCargo(numberCargo);
        generateRobots(numberRobots);
    }
    public void init(List<Robot> list){
        generateCargo(numberCargo);
        RobotList.clear();
        robotBackUp.clear();
        for (Robot r : list) {
            Robot robot = new RobotCargo(r.getPosition(),robotCapacity);
            points.put(robot,robot.getPosition());
            RobotList.add(robot);
            robotBackUp.add(robot);
        }
    }



    @Override
    public void run() {
        try {
            test();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long call() throws Exception {
        long time = 0, before, after;
        before = System.currentTimeMillis();
        logger.info("Running robots...");
        runRobots();
        logger.info("Done");
        after = System.currentTimeMillis();
        time = after - before;
        return time;
    }
}
