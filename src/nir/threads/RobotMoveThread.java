package nir.threads;

import nir.model.Robot;
import nir.model.Route;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.concurrent.Callable;

public class RobotMoveThread implements Callable<Long>, Runnable {
    final static Logger logger = Logger.getLogger(RobotMoveThread.class);


    private Robot robot;
    private Coordinate coord;
    private boolean onRoute = false;

    public void setRoute(Route route) {
        robot.setRoute(route);
        onRoute = true;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
        onRoute = false;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
        if (robot.getRoute() != null) onRoute = true;
    }


    @Override
    public Long call() throws Exception {
        long before = System.currentTimeMillis();
        if (onRoute) robot.moveRoute();
        else robot.move(coord);
        long after = System.currentTimeMillis();
        return after - before;
    }

    @Override
    public void run() {
        long before = System.currentTimeMillis();
        if (onRoute) robot.moveRoute();
        else robot.move(coord);
        long after = System.currentTimeMillis();
        logger.info("robot "+ robot + " time: " + (after - before));
    }
}