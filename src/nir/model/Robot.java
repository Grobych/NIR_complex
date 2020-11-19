package nir.model;

import nir.list.CargoList;
import nir.list.ObstacleList;
import nir.model.map.Cargo;
import nir.model.map.MapHolder;
import nir.util.Intersection;
import nir.util.Mat;
import org.locationtech.jts.geom.Coordinate;

import java.util.Random;

public class Robot extends AbstractObject implements Movable {

    protected int speed = 10;
    protected double rotation;
    private static final int size = 5;

    private Route route;

    public Robot(Coordinate coordinate){
        super(coordinate);
        this.route = new Route();
    }

    @Override
    public boolean move(Coordinate coordinate) {
        return move(coordinate.x, coordinate.y);
    }

    public double getRotation() {
        //System.out.println(rotation);
        return rotation;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }

    public boolean move(double destX, double destY) {
        checkCargo();
        while (getX() != destX || getY() != destY) {
            if ((Math.abs(destX - getX()) < speed) && (Math.abs(destY - getY()) < speed)) {
                setPosition(new Coordinate(destX,destY));
                return true;
            }
            double rad = Mat.getRad(getX(), getY(), destX, destY);
            rotation = Math.toDegrees(rad) + 90;
            double coef =  MapHolder.INSTANCE.getLevelMap().getLineLevelUpping(getPosition(), new Coordinate(destX,destY));
            if (coef == 0) coef = 1;
            if (coef > 20) coef = 20;
            coef = 1 / ((coef + 3)/4);
//            System.out.println(coef);
            move(rad, (int)(speed * coef));
            if (Intersection.isIntersect(this.getPosition(), ObstacleList.getObstacles())) {
//                System.out.println("Intersect " + this.getPosition());
                moveBack();
                //return false;
            }
            try {
                Thread.sleep(10); // TODO 100
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    protected void checkCargo() {
        for (Cargo cargo : CargoList.cargoList) {
            if (this.getPosition().distance(cargo.getPosition()) < 5) cargo.setTaken(true);
            if (this.getPosition().distance(cargo.getGoal().getPosition()) < 5) {
                cargo.setDelivered(true);
                cargo.setPosition(this.getPosition());
            }
        }
    }

    protected boolean moveBack() {
        double newX, newY;
        int i = 0;
        do {
            i++;
            if (i > 20) return false;
            Double rad = this.getRotation() - 180 + new Random().nextInt(180) - 90;
            newX = (this.getPosition().x + 15 * Math.cos(rad));
            newY = (this.getPosition().y + 15 * Math.sin(rad));
        } while (Intersection.isIntersect(new Coordinate(newX, newY), ObstacleList.getObstacles()));
//        System.out.println("Current: " + getPosition() + "   New: "+ newX + " "+ nexY);
        return this.move(newX, newY);
    }

    public void moveRoute(Route route) {
        setRoute(route);
        moveRoute();
    }

    public void moveRoute() {
        for (Coordinate point : route.list) {
            move(point);
        }
    }

    public void move(double rad, int speed) {
//        this.rotation = rad;
        double xPosition = (getX() + speed * Math.cos(rad));
        double yPosition = (getY() + speed * Math.sin(rad));
        setPosition(new Coordinate(xPosition,yPosition));
    }
}
