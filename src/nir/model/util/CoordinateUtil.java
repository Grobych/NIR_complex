package nir.model.util;

import nir.model.list.ObstacleList;
import org.locationtech.jts.geom.Coordinate;

import java.util.Random;

public class CoordinateUtil {
    public static Coordinate randCoordinate(int x1, int y1, int x2, int y2) {
        boolean b = false;
        Coordinate c;
        do {
            double rx = new Random().nextDouble()*(x2-x1) + x1;
            double ry = new Random().nextDouble()*(y2-y1) + y1;
            c = new Coordinate(rx,ry);
            if (!Intersection.isIntersect(c, ObstacleList.getObstacles())) b = true;
        } while (!b);
        return c;
    }
}
