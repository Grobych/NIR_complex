package nir.model.list;

import nir.model.base.Route;

import java.util.ArrayList;
import java.util.List;

import static nir.model.util.RouteUtil.calculateRouteLenght;

public class RouteList {
    private List<Route> list = new ArrayList<>();
//    private static double bestLenght = Double.MAX_VALUE;
//    private static Route bestRoute;

    public synchronized void add(Route route) {
        Route newRoute = new Route(route);
        list.add(newRoute);
//        System.out.println(list.size() + "   " + route.list.size());
        double newL = calculateRouteLenght(route);
//        System.out.println(newL);
//        if (bestLenght > newL){
//            bestLenght = newL;
//            bestRoute = new Route(route.list);
//        }
    }



    public Route get(int i) {
        if (i < list.size() && i >= 0) return list.get(i);
        else return null;
    }

    public Route getBestRoute() {
        Route bestRoute = list.get(0);
        double best = calculateRouteLenght(bestRoute.list);
//        System.out.println("Size:   " + best);
        for (Route route : list) {
            double v = calculateRouteLenght(route);
            if (v < best) {
                bestRoute = route;
                best = v;
            }
        }
        return bestRoute;
    }
}
