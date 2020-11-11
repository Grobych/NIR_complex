package nir.model.global;

public class GlobalVariables {
    private static GlobalVariables instanse = new GlobalVariables();

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
