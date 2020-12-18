package nir.model.algorithms.swarmmodel;

import nir.model.algorithms.antmodel.RobotAgent;
import nir.model.global.GlobalVariables;
import org.locationtech.jts.geom.Coordinate;

import java.util.Random;

public class RobotParticle extends RobotAgent {
    public RobotParticle(Coordinate coordinate) {
        super(coordinate);
    }

    public void setRandomSpeed(){
        this.speed = new Random().nextInt(GlobalVariables.getInstance().get("movingDist").intValue());
        this.rotation = new Random().nextInt(360);
    }

    @Override
    public boolean move(Coordinate coordinate){
        movedRoute.add(coordinate);
        return super.move(coordinate);
    }
}