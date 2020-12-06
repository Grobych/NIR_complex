package nir.algorithms.antmodel;

import nir.list.ObstacleList;
import nir.model.Robot;
import nir.model.Route;
import nir.util.Intersection;
import nir.util.Mat;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class RobotAgent extends Robot {

    private boolean isGoalTaken;
    private int up = 0;
    protected List<Coordinate> movedRoute = new ArrayList<>();

    public RobotAgent(Coordinate coordinate) {
        super(coordinate);
    }

    public RobotAgent(int x, int y) {
        super(new Coordinate(x, y));
        this.speed = 2;
        movedRoute.add(new Coordinate(x, y));
    }

    public void incUp(int val) {
        this.up += val;
    }

    public int getUp() {
        return up;
    }


    public void setGoalTaken(boolean goalTaken) {
        isGoalTaken = goalTaken;
    }

    public boolean isGoalTaken() {
        return isGoalTaken;
    }

    public void clearMovedRoute() {
        movedRoute.clear();
    }

    public void redirect(Coordinate coordinate) {
        this.setPosition(coordinate);
        isGoalTaken = false;
        up = 0;
    }

    public int getMoveSize() {
        return movedRoute.size();
    }

    public List<Coordinate> getMovedRoute() {
        return this.movedRoute;
    }

    @Override
    public boolean move(Coordinate coordinate){
        return this.move(coordinate.x,coordinate.y);
    }

    @Override
    public boolean move(double destX, double destY) {
        int i = 0;
        Coordinate start = getPosition();
        while (getX() != destX || getY() != destY) {
            if (++i == 30) return false;
            if ((Math.abs(destX - getX()) < speed) && (Math.abs(destY - getY()) < speed)) {
                setPosition(new Coordinate(destX, destY));
                return true;
            }
            double rad = Mat.getRad(getX(), getY(), destX, destY);
            rotation = Math.toDegrees(rad) + 90;
            move(rad, speed);
            if (Intersection.isIntersect(this.getPosition(), ObstacleList.getObstacles())) {
//                System.out.println("Intersect " + this.getPosition());
                if (!moveBack()) setPosition(start);
                return false;
            }
        }
        return true;
    }

    @Override
    public void moveRoute(Route route){
        for (Coordinate coordinate : route.list) {
            move(coordinate);
        }
    }
}
