package nir.model.algorithms;

import nir.model.base.Route;
import nir.model.global.Variable;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public abstract class Routing {
    public abstract Route getRoute(Coordinate start, Coordinate end);
    public abstract void initParams(List<Variable> list);
}
