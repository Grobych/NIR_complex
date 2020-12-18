package nir.model.base;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.util.ArrayList;
import java.util.List;

public class Route {
    public List<Coordinate> list = new ArrayList<>();

    public Route(){}
    public Route(List<Coordinate> coords) {
        this.list = coords;
    }
    public Route(Route route) {
        for (Coordinate c : route.list) {
            this.list.add(new Coordinate(c.x, c.y));
        }
    }
    public void add(Coordinate c){
        list.add(c);
    }
    public LineString getLineString() {
        return new GeometryFactory().createLineString(list.toArray(new Coordinate[list.size()]));
    }
    public void addRoute(Route route){
        list.addAll(route.list);
    }
}
