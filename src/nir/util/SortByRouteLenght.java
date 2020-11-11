package nir.util;

import nir.model.Route;

import java.util.Comparator;

public class SortByRouteLenght implements Comparator<Route> {
    @Override
    public int compare(Route r1, Route r2) {
        double s1 = RouteUtil.calculateRouteLenght(r1);
        double s2 = RouteUtil.calculateRouteLenght(r2);
        if ( s1 < s2 ) return -1;
        else if ( s1 == s2 ) return 0;
        else return 1;
    }
}
