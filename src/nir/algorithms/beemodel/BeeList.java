package nir.algorithms.beemodel;

import nir.algorithms.antmodel.RobotAgent;

import java.util.ArrayList;
import java.util.List;

public class BeeList {
    public static List<RobotAgent> agents = new ArrayList<>();
    public static List<Bee> bees = new ArrayList<>();


    public static void addAgent(RobotAgent agent) {
        agents.add(agent);
    }
    public static RobotAgent getAgent(int i){
        return agents.get(i);
    }
    public static void addBee(Bee bee) { bees.add(bee);}
    public static Bee getBee(int i) { return bees.get(i);}
    public static void clearAll(){
        agents.clear();
        bees.clear();
    }
}
