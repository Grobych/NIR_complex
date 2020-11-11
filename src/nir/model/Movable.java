package nir.model;

import org.locationtech.jts.geom.Coordinate;

public interface Movable {
    boolean move(Coordinate coordinate);
}
