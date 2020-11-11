package nir.algorythms.swarmmodel;

import java.util.ArrayList;
import java.util.List;

public class ParticlesList {
    public static List<RobotParticle> list = new ArrayList<>();

//    public static void init(int n){
//        int maxX = MapHolder.INSTANCE.getLevelMap().getxSize();
//        int maxY = MapHolder.INSTANCE.getLevelMap().getySize();
//        for (int i = 0; i < n; i++) {
//            Coordinate c = CoordinateUtil.randCoordinate(0,0,maxX,maxY);
//            RobotParticle particle = new RobotParticle(c);
//            list.add(particle);
//        }
//    }

    public static void add(RobotParticle particle) {
        list.add(particle);
    }
    public static RobotParticle get(int i){
        return list.get(i);
    }
    public static void clear(){
        list.clear();
    }
}
