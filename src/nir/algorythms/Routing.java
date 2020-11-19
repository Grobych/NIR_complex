package nir.algorythms;

import nir.model.Route;
import org.locationtech.jts.geom.Coordinate;

public abstract class Routing {
    public abstract Route getRoute(Coordinate start, Coordinate end);
}
