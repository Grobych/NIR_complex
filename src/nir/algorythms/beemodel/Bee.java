package nir.algorythms.beemodel;

import nir.algorythms.antmodel.RobotAgent;
import nir.model.Route;
import nir.model.global.GlobalVariables;
import org.locationtech.jts.geom.Coordinate;

import java.util.ConcurrentModificationException;
import java.util.Random;

public class Bee extends RobotAgent {

    public Bee(Coordinate coordinate) {
        super(coordinate);
    }

    @Override
    public synchronized void moveRoute(Route route){
        try {
            for (Coordinate coordinate : route.list) {
                double r = new Random().nextDouble();
                Coordinate c = new Coordinate();
                c.x = coordinate.x;
                c.y = coordinate.y;
                if (r < 0.25) {
                    double dist = GlobalVariables.getInstance().movingDist;
                    double dX = new Random().nextDouble() * dist - 2 * dist;
                    double dY = new Random().nextDouble() * dist - 2 * dist;
                    c.x = c.x + dX;
                    c.y = c.y + dY;
                }
                move(c);
                movedRoute.add(c);
            }
        } catch (ConcurrentModificationException e){

        }

    }
}
