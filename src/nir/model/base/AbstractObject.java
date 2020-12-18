package nir.model.base;

import org.locationtech.jts.geom.Coordinate;

public abstract class AbstractObject {
    private Coordinate position;

    public AbstractObject(Coordinate position){
        this.position = position;
    }
    public AbstractObject(){
        this.position = new Coordinate(0,0);
    }

    public Coordinate getPosition() {
        return position;
    }
    public double getX(){
        return position.x;
    }
    public double getY(){
        return position.y;
    }

    public void setPosition(Coordinate position){
        this.position = position;
    }
}
