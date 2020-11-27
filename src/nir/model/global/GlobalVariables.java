package nir.model.global;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GlobalVariables {
    private static GlobalVariables instanse = new GlobalVariables();
    public ObservableList<Variable> list = FXCollections.observableArrayList();
    public String antParams[] = {
            "numberRobots",
            "agentsNumber",
            "iterations",
            "phInit",
            "phThreshold",
            "phEx",
            "movingDist",
            "movedCoordCoef",
            "goal",
            "ph",
            "rand"};
    public String swampParams[] = {
            "numberRobots",
            "agentsNumber",
            "iterations",
            "toRCoef",
            "toGCoef",
            "steps",
            "movingDist"
    };
    public String beeParams[] = {
            "numberRobots",
            "agentNumber",
            "beeNumber",
            "iterations",
            "movingDist"
    };

    public GlobalVariables(){
        this.list.add(new Variable("numberRobots",10d));
        this.list.add(new Variable("robotCapasity",1d));
        this.list.add(new Variable("agentsNumber",10d));
        this.list.add(new Variable("iterations",100d));

        this.list.add(new Variable("phInit",1d));
        this.list.add(new Variable("phThreshold",0.3d));
        this.list.add(new Variable("phEx",16000d));
        this.list.add(new Variable("movingDist",20d));
        this.list.add(new Variable("movedCoordCoef",15d));
        this.list.add(new Variable("goal",0.3d));
        this.list.add(new Variable("ph",1d));
        this.list.add(new Variable("rand",1d));

        this.list.add(new Variable("toRCoef", 0.8));
        this.list.add(new Variable("toGCoef", 0.2));
        this.list.add(new Variable("steps", 300d));

        this.list.add(new Variable("beeAgentNumber", 5d));
        this.list.add(new Variable("beeNumber", 10d));


    }
    public static GlobalVariables getInstance(){
        return instanse;
    }

    protected static void load(GlobalVariables variables){
        instanse = variables;
    }

    public int numberRobots = 10;
    public int robotCapasity = 1;
    public int agentsNumber = 10;
    public int iterations = 100;

    // 1 part

    public double phInit = 1;
    public double phThreshold = 0.3;
    public double phEx = 16000;
    public double movingDist = 20;
    public double movedCoordCoef = 15;
    public double goal = 0.3;
    public double ph = 1;
    public double rand = 1;


    // 2 part

    public double toRCoef = 0.8;
    public double toGCoef = 0.2;
    public int steps = 300;

}
