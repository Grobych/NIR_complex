package nir.algorythms;

import nir.model.Route;
import nir.model.global.Variable;
import nir.model.global.VariableList;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;
import java.util.concurrent.Callable;

public class BaseRouting extends Routing implements Callable<Route> {
    protected Coordinate start, end;
    protected VariableList params;

    protected BaseRouting(Coordinate start, Coordinate end, List<Variable> variableList){
        this.setPoints(start,end);
        initParams(variableList);
    }
    @Override
    public Route getRoute(Coordinate start, Coordinate end) {
        return null;
    }

    @Override
    public void initParams(List<Variable> list) {
        this.params.load(list);
    }

    public void setPoints(Coordinate start, Coordinate end){
        this.start = start;
        this.end = end;
    }

    @Override
    public Route call() throws Exception {
        return null;
    }
}
