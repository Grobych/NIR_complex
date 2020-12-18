package nir.model.util;

import nir.model.base.Obstacle;
import org.locationtech.jts.geom.*;

import java.util.List;

public class Intersection {
    private static GeometryFactory gf = new GeometryFactory();

    public static boolean isIntersect(LineString line, Obstacle obstacle) {
        return obstacle.getPolygon().getBoundary().intersects(line);
    }

    public static Coordinate[] getIntersect(LineString line, Obstacle obstacle) {
        if (isIntersect(line, obstacle)) return obstacle.getPolygon().getBoundary().intersection(line).getCoordinates();
        else return null;
    }

    public static boolean isIntersect(LineSegment line, Obstacle obstacle) {
        return obstacle.getPolygon().getBoundary().intersects(line.toGeometry(gf));
    }

    public static boolean isIntersect(Coordinate coordinate, Obstacle obstacle) {
        Point point = gf.createPoint(coordinate);
        return point.within(obstacle.getPolygon());
    }

    public static boolean isIntersect(Coordinate point, List<Obstacle> list) {
        for (Obstacle obstacle : list) {
            if (isIntersect(point, obstacle)) return true;
        }
        return false;
    }

    public static boolean isIntersect(LineSegment line, List<Obstacle> list) {
        for (Obstacle obstacle : list) {
            if (isIntersect(line, obstacle)) return true;
        }
        return false;
    }
}
