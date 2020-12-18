package nir.model.base;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

public class PolygonObject extends AbstractObject {
    Polygon polygon;

    private void toRing(Coordinate[] coords){
        LinearRing ring = new GeometryFactory().createLinearRing(coords);
        this.polygon = new GeometryFactory().createPolygon(ring);
    }

    public PolygonObject(){
        super();
        Coordinate coords[] = {
                new Coordinate(0,0),
                new Coordinate(0,1),
                new Coordinate(1,1),
                new Coordinate(1,0)
        };
        this.toRing(coords);
    }

    public PolygonObject(Coordinate[] coords) {
        super(coords[0]);
        this.toRing(coords);
    }
    public PolygonObject(Coordinate pos, Coordinate[] coords){
        super(pos);
        for (int i = 0; i < coords.length; i++) {
            double x = coords[i].x;
            double y = coords[i].y;
            coords[i] = new Coordinate(pos.x + x, pos.y + y);
        }
        toRing(coords);
    }
    public PolygonObject(Coordinate pos, int size){
        super(pos);
        Coordinate[] c = {
                new Coordinate(0,0),
                new Coordinate(0,size),
                new Coordinate(size,size),
                new Coordinate(size,0)
        };
        this.toRing(c);
    }

    public Coordinate[] getCoordinates(){
        return this.polygon.getCoordinates();
    }
    public Polygon getPolygon(){
        return polygon;
    }
}
