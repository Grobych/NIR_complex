package nir.model.base;

import org.locationtech.jts.geom.Coordinate;

public interface Movable {
    boolean move(Coordinate coordinate);
}
