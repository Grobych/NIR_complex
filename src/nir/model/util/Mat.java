package nir.model.util;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Mat {
    public static double getRad(double x1, double y1, double x2, double y2) {
        double difX = x2 - x1;
        double difY = y2 - y1;
        return Math.atan2(difY, difX);
    }

    public static List<Coordinate> line_s4(int x1, int y1, int x2, int y2) {
        List<Coordinate> res = new ArrayList<>();

        int x = x1, y = y1;
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = (x2 - x1) > 0 ? 1 : ((x2 - x1) == 0 ? 0 : -1);
        int sy = (y2 - y1) > 0 ? 1 : ((y2 - y1) == 0 ? 0 : -1);
        int e = 2 * dy - dx;
        boolean change = false;
        if (dy > dx) {
            int z = dx;
            dx = dy;
            dy = z;
            change = true;
        }
        res.add(new Coordinate(x, y));
        for (int k = 1; k <= (dx + dy); k++) {
            if (e < dx) {
                if (change) y += sy;
                else x += sx;
                e += 2 * dy;
            } else {
                if (change) x += sx;
                else y = y + sy;
                e -= 2 * dx;
            }
            res.add(new Coordinate(x, y));
        }
        return res;
    }
}
