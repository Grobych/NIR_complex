package nir.model;

import org.locationtech.jts.geom.Coordinate;

public class Goal extends AbstractObject {
    private int R;

    public Goal(Coordinate c) {
        super(c);
        this.R = 3;
    }

    public Goal(int x, int y) {
        this.setPosition(new Coordinate(x, y));
        this.R = 3;
    }

    public int getR() {
        return R;
    }
}
