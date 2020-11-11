package nir.util;

import nir.model.Route;
import nir.model.map.MapHolder;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public class RouteUtil {

    public synchronized static Double calculateRouteLenght(List<Coordinate> list){
        Double res = 0d;
        if (list.size() == 0) return Double.MAX_VALUE;
        for (int i = 0; i < list.size() - 1; i++) {
            Coordinate c1 = list.get(i);
            Coordinate c2 = list.get(i + 1);
            res += c1.distance(c2);
            res += MapHolder.INSTANCE.getLevelMap().getLineLevelUpping(c1, c2) * 6;
        }
        return res;
    }
    public synchronized static Double calculateRouteLenght(Route route) {
        return calculateRouteLenght(route.list);
    }

    public synchronized static Route bestRoute(Route r1, Route r2){
        double d1 = calculateRouteLenght(r1);
        double d2 = calculateRouteLenght(r2);
        if (d1 < d2) return r1;
        else return r2;
    }

}
