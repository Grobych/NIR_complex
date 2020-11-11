package nir.threads;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import nir.algorythms.swarmmodel.RobotParticle;
import nir.list.CargoList;
import nir.list.GoalList;
import nir.list.ObstacleList;
import nir.list.RobotList;
import nir.model.Goal;
import nir.model.Obstacle;
import nir.model.Robot;
import nir.model.map.Cargo;
import nir.model.map.MapHolder;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public class RenderThread extends Thread {
    final static Logger logger = Logger.getLogger(RenderThread.class);
    Image robotImage = new Image("file:robot.jpg");
    Image levelMapImage = new Image("file:levelmap2.jpg");
    Image goalImage = new Image("file:resources\\star.png");
    Image cargoImage = new Image("file:resources\\Box.png");

    GraphicsContext mapGC, robotGC, utilGC;// phGC;
    private boolean isRun = false;

    @FXML
    Canvas mapCanvas;
    @FXML
    Canvas robotCanvas;
    @FXML
    Canvas utilCanvas;
//    @FXML
//    Canvas pheromoneCanvas;

    public void setCanvases(Canvas map, Canvas robot, Canvas util) {
        this.mapCanvas = map;
        this.robotCanvas = robot;
        this.utilCanvas = util;
        mapGC = mapCanvas.getGraphicsContext2D();
        robotGC = robotCanvas.getGraphicsContext2D();
        utilGC = utilCanvas.getGraphicsContext2D();
        utilCanvas.toFront();
    }


    @Override
    public void run() {
        isRun = true;
        logger.info("Render thread is started...");
        drawMapCanvas();
        while (isRun) {
            clear(robotGC);
            clear(utilGC);
            //drawGoals();
            drawCargos();
            drawRobots();
//            drawParticles(ParticlesList.list);
//            drawPheromone();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }
    private void clear(GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
    synchronized private void drawRobots(){
        drawRobots(RobotList.getRobotList());
    }
    synchronized private void drawRobots(List<Robot> list) {
        if (list == null) return;
        for (Robot robot : list) {
            drawRotatedImage(robotGC, robotImage, robot.getRotation(), robot.getX() - robotImage.getWidth() / 2, robot.getY() - robotImage.getHeight() / 2);
            robotGC.save();
        }
    }
    synchronized private void drawParticles(List<RobotParticle> list){
        if (list == null) return;
        for (Robot robot : list) {
            drawRotatedImage(robotGC, robotImage, robot.getRotation(), robot.getX() - robotImage.getWidth() / 2, robot.getY() - robotImage.getHeight() / 2);
            robotGC.save();
        }
    }
    public void drawGoals() {
        for (Goal goal : GoalList.goalList) {
            drawGoal(goal);
        }
    }
    public synchronized void drawGoal(Goal goal) {
        utilGC.drawImage(goalImage, goal.getX() - 15, goal.getY() - 15, 30, 30);
    }
    public synchronized void drawCargos() {
        for (Cargo cargo : CargoList.cargoList) {
            drawCargo(cargo);
        }
    }
    public synchronized void drawCargo(Cargo cargo) {
        if (!cargo.isTaken()) utilGC.drawImage(cargoImage, cargo.getX() - 16, cargo.getY() - 16, 32, 32);
        if (!cargo.isDelivered()) drawGoal(cargo.getGoal());

    }


    private void drawMapCanvas() {
        drawMap();
        drawObstacles();
    }

    private void drawMap() {
        drawLevelMap();
    }
    private void drawLevelMap() {
        mapGC.drawImage(levelMapImage, 0, 0, MapHolder.INSTANCE.getLevelMap().getxSize(), MapHolder.INSTANCE.getLevelMap().getySize());
    }

    private void drawObstacles() {
        for (Obstacle obstacle : ObstacleList.getObstacles()) {
            Coordinate[] coordinates = obstacle.getCoordinates();
            int size = obstacle.getCoordinates().length;
            double[] x = new double[size];
            double[] y = new double[size];
            for (int i = 0; i < coordinates.length; i++) {
                x[i] = coordinates[i].x;
                y[i] = coordinates[i].y;
            }
            mapGC.fillPolygon(x, y, size);
        }
        mapGC.save();
    }

    public void stopThread() {
        isRun = false;
        logger.info("Render thread stopped.");
        //System.out.println(isRun);
    }



    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
        gc.save(); // saves the current state on stack, including the current transform
        rotate(gc, angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        gc.drawImage(image, tlpx, tlpy);
        gc.restore(); // back to original state (before rotation)
    }
}
