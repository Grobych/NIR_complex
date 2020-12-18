package nir.threads;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;
import nir.model.algorithms.swarmmodel.RobotParticle;
import nir.controller.ImageController;
import nir.model.list.CargoList;
import nir.model.list.GoalList;
import nir.model.list.ObstacleList;
import nir.model.list.RobotList;
import nir.model.base.Goal;
import nir.model.base.Obstacle;
import nir.model.base.Robot;
import nir.model.base.Route;
import nir.model.base.Cargo;
import nir.model.map.MapHolder;
import nir.model.util.logging.Log;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class RenderThread extends Thread implements Observer {
    Image robotImage = new Image("file:robot.jpg");
    Image levelMapImage = new Image("file:levelmap2.jpg");
    Image goalImage = new Image("file:resources\\star.png");
    Image cargoImage = new Image("file:resources\\Box.png");

    GraphicsContext mapGC, robotGC, utilGC, routeGC;// phGC;
    private boolean isRun = false;

    private Route route;
    Coordinate start, end;

    @FXML
    Canvas mapCanvas;
    @FXML
    Canvas robotCanvas;
    @FXML
    Canvas utilCanvas;
    @FXML
    Canvas routeCanvas;
//    @FXML
//    Canvas pheromoneCanvas;

    public void setCanvases(Canvas map, Canvas robot, Canvas util, Canvas route) {
        this.mapCanvas = map;
        this.robotCanvas = robot;
        this.utilCanvas = util;
        this.routeCanvas = route;
        mapGC = mapCanvas.getGraphicsContext2D();
        robotGC = robotCanvas.getGraphicsContext2D();
        utilGC = utilCanvas.getGraphicsContext2D();
        routeGC = routeCanvas.getGraphicsContext2D();
        utilCanvas.toFront();
    }


    @Override
    public void run() {
        isRun = true;
        Log.info("Render thread is started...");
        drawMapCanvas();
        while (isRun) {
            clear(robotGC);
            clear(utilGC);
            //drawGoals();
            drawStartEnd();
            drawCargos();
            drawRobots();
            drawSingleRoute();
//            drawParticles(ParticlesList.list);
//            drawPheromone();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.error(e.getMessage());
            }
        }
    }

    private void drawSingleRoute() {
        Route fromIC = ImageController.route;
        if (fromIC != null){
            if (fromIC != this.route){
                clear(routeGC);
                drawRoute(fromIC);
                this.route = fromIC;
            }
        }
    }
    public void drawRoute(Route route) {
        //System.out.println(route.list );
        Coordinate startPoint = route.list.get(0);
        routeGC.moveTo(startPoint.getX(), startPoint.getY());
        routeGC.setStroke(Paint.valueOf("#FF0000"));
        for (Coordinate o : route.list) {
            double nextX = o.x;
            double nextY = o.y;
            routeGC.lineTo(nextX, nextY);
            routeGC.stroke();
        }
        routeGC.setStroke(Paint.valueOf("#000000"));
    }

    private void drawStartEnd() {
        Coordinate start = ImageController.start;
        Coordinate end = ImageController.end;
        if (start != null) utilGC.drawImage(goalImage, start.x - 10,start.y-10,20,20);
        if (end != null) utilGC.drawImage(goalImage, end.x - 10,end.y - 10, 20,20);
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
        Log.info("Render thread stopped.");
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

    @Override
    public void update(Observable observable, Object o) {
        Log.debug("loading image");
        mapGC.clearRect(0,0,levelMapImage.getWidth(),levelMapImage.getHeight());
        levelMapImage = new Image("file:"+MapHolder.INSTANCE.getImageMap().getFileName());
        drawMap();
    }
}
