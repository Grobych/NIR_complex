package nir.model.base;

import org.locationtech.jts.geom.Coordinate;

public class Cargo extends AbstractObject {
    private boolean isTaken;
    private boolean isDelivered;
    private Goal goal;

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public Goal getGoal() {
        return goal;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public boolean isTaken() {
        return isTaken;
    }


    public Cargo(Coordinate c, Coordinate goal){
        super(c);
        this.goal = new Goal(goal);
        isDelivered = false;
        isTaken = false;
    }
}
