package nir.algorythms.antmodel;

import nir.list.RouteList;
import nir.model.Route;
import nir.model.map.MapHolder;
import nir.threads.AntAgentThread;
import nir.util.RouteUtil;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Pair;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class AntRouting implements Callable<Route> {
    private volatile Route result;
    public RouteList routeList = new RouteList();

    public static List<RobotAgent> agents;
    public Coordinate start, goal;
    private int iterationNumber, currentIteration;
    private int agentsNumber;
    private double phInit = 1;
    private double phThreshold = 0.3;
    public double phEx = 16000;
    private double movingDist = 20;
    private double movedCoordCoef = 15;
    private Pheromone pheromone = new Pheromone();

    private double gC, phC, rC;

    protected static boolean goalTaken = false;

    public static void setGoalTaken(boolean goalTaken) {
        AntRouting.goalTaken = goalTaken;
    }

    public static boolean isGoalTaken() {
        return goalTaken;
    }

    public Coordinate getStart() {
        return this.start;
    }

    public Coordinate getGoal() {
        return goal;
    }

    public Route getResult() {
        return result;
    }

    public AntRouting(Coordinate start, Coordinate goal, int iterations, int agentsNumber, double phInit) {
        this.start = start;
        this.goal = goal;
        this.iterationNumber = iterations;
        this.agentsNumber = agentsNumber;
        this.phInit = phInit;
        createAgents();
    }

    public void setParams(double goal, double ph, double rand) {
        this.gC = goal;
        this.phC = ph;
        this.rC = rand;
    }

    public void stop() {
        pheromone.clear();
        agents.clear();
        createAgents();
    }

    @Override
    public Route call() {
        AgentChecker.setN(agentsNumber);
        for (int i = 0; i < iterationNumber; i++) {
            List<Thread> threads = new ArrayList<>();
            for (RobotAgent agent : agents) {
                AntAgentThread agentThread = new AntAgentThread();
                agentThread.setAgent(agent);
                agentThread.setRouting(this);
                Thread thread = new Thread(agentThread);
                threads.add(thread);
            }
            threads.forEach(Thread::start);
            while (!AgentChecker.isDone()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (RobotAgent agent : agents) {
                if (agent.isGoalTaken()){
                    pheromone.expandPheromone(agent,getStart(),phEx * 2);
                }
                else {
                    pheromone.expandPheromone(agent,getStart(),phEx / 4);
                }
            }
            try {
                pheromone.unitePheromone();
                pheromone.evaporatePheromone(phThreshold);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (RobotAgent agent : agents) {
                redirect(agent,start);
            }
            AgentChecker.reset();
        }
        System.out.println("Step from " + start + " to " + goal + " done");
        result = routeList.getBestRoute();
        result.add(goal);
        return result;
    }






    public static Double calculateRouteLenght(RobotAgent agent) {
        return RouteUtil.calculateRouteLenght(agent.getMovedRoute());
    }

//    public static Double calculateRouteLenght(List<Coordinate> list) {
//        Double res = 0d;
//        for (int i = 0; i < list.size() - 1; i++) {
//            Coordinate c1 = list.get(i);
//            Coordinate c2 = list.get(i + 1);
//            res += c1.distance(c2);
//            res += Map.getLineLevelUpping(c1, c2) * 3;
//        }
//        return res;
//    }

    public boolean agentMoveTooLong(RobotAgent agent) {
        return agent.getMoveSize() > 200;
    }

    public boolean goalTaken(RobotAgent agent) {
        return goal.distance(agent.getPosition()) < 20;
    }

    public void redirect(RobotAgent agent, Coordinate start) {
        agent.clearMovedRoute();
        agent.redirect(start);
    }

    synchronized public Coordinate getCoordinate(RobotAgent agent, Coordinate goal) {
        Coordinate res;
        int i = 0;
        do {
            double[] goalVector = getRotation(agent, goal);
            double[] pheromoneVector = getRotation(agent);
            double[] randomVector = getRotation();
            res = choosePoint(goalVector,pheromoneVector,randomVector,agent);
        } while(checkIsInRoute(res,agent.getMovedRoute()) && (i++ < 10));
        return res;
    }

    private Coordinate choosePoint(double[] goalVector, double[] pheromoneVector, double[] randomVector, RobotAgent agent) {

        List<Pair<Vector2D, Double>> pairs = new ArrayList<>();
        Vector2D v1 = new Vector2D(goalVector[0],goalVector[1]);
        Vector2D v2 = new Vector2D(pheromoneVector[0],pheromoneVector[1]);
        Vector2D v3 = new Vector2D(randomVector[0],randomVector[1]);

        double d1 = MapHolder.INSTANCE.getLevelMap().getLineLevelUpping(agent.getPosition(),new Coordinate(agent.getPosition().x + v1.getX(), agent.getPosition().y + v1.getY()));
        double d2 = MapHolder.INSTANCE.getLevelMap().getLineLevelUpping(agent.getPosition(),new Coordinate(agent.getPosition().x + v2.getX(), agent.getPosition().y + v2.getY()));
        double d3 = MapHolder.INSTANCE.getLevelMap().getLineLevelUpping(agent.getPosition(),new Coordinate(agent.getPosition().x + v3.getX(), agent.getPosition().y + v3.getY()));

        pairs.add(new Pair<>(v1,gC * 1/(d1+1)));
        pairs.add(new Pair<>(v2,phC * 1/(d2 + 1)));
        pairs.add(new Pair<>(v3,rC * 1/(d3 + 1)));

        EnumeratedDistribution enumeratedDistribution = new EnumeratedDistribution<Vector2D>(pairs);
        Vector2D res = (Vector2D) enumeratedDistribution.sample();
        return new Coordinate(agent.getPosition().x + res.getX(), agent.getPosition().y + res.getY());
    }

    private boolean checkIsInRoute(Coordinate res, List<Coordinate> movedRoute) {
        for (Coordinate c : movedRoute) {
            if (c.distance(res) < movedCoordCoef) return true;
        }
        return false;
    }

    private Coordinate calculatePoint(double[] v1, double[] v2, double[] v3, RobotAgent agent) {
        double[] resV = new double[2];
        resV[0] = v1[0] * gC + v2[0] * phC + v3[0] * rC;
        resV[1] = v1[1] * gC + v2[1] * phC + v3[1] * rC;
        Coordinate result = new Coordinate(agent.getPosition().x + resV[0], agent.getPosition().y + resV[1]);
        return result;
    }

    private double[] checkHight(RobotAgent agent, double[] v) {
        int x1 = (int) agent.getPosition().x;
        int y1 = (int) agent.getPosition().y;
        int x2 = x1 + (int) v[0];
        int y2 = y1 + (int) v[1];
        Coordinate c = new Coordinate(x2,y2);
        int delta = MapHolder.INSTANCE.getLevelMap().getLineLevelUpping(agent.getPosition(), c);
//        System.out.println("delta: "+delta);
//        System.out.println("before: "+v[0]+" "+v[1]);
        if (delta > 5) {
            v[0] = v[0] * (6d / delta);
            v[1] = v[1] * (6d / delta); //TODO: choose different coef
        }
//        System.out.println("after: "+v[0]+" "+v[1]);
        return v;
    }

    private double higthCoeff(RobotAgent agent, double[] v){
        int x1 = (int) agent.getPosition().x;
        int y1 = (int) agent.getPosition().y;
        int x2 = x1 + (int) v[0];
        int y2 = y1 + (int) v[1];
        double delta = MapHolder.INSTANCE.getLevelMap().getLevelUping(x1, y1, x2, y2);
        return delta;
    }

    private double[] getRotation(RobotAgent agent, Coordinate goal) {
        Coordinate r = agent.getPosition();
        double[] vector = new double[2];
        vector[0] = (goal.x - r.x) / r.distance(goal) * movingDist ;
        vector[1] = (goal.y - r.y) / r.distance(goal) * movingDist ;
        return vector;
    }

    synchronized private double[] getRotation(RobotAgent agent) {
        double[] vector = new double[2];
        double[] temp = new double[2];
        try {
            pheromone.phMutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Coordinate coordinate : pheromone.key()) {
            double d = agent.getPosition().distance(coordinate);
            if (d < movingDist) {
                temp[0] = coordinate.x - agent.getPosition().x;
                temp[1] = coordinate.y - agent.getPosition().y;
                vector[0] += temp[0] * pheromone.get(coordinate) / 2 /d;
                vector[1] += temp[1] * pheromone.get(coordinate) / 2 /d;
            }
        }
        pheromone.phMutex.release();

        return vector;
    }

    private double[] getRotation() {
        double[] vector = new double[2];
        vector[0] = new Random().nextDouble() * movingDist * 2 - movingDist;
        vector[1] = new Random().nextDouble() * movingDist * 2 - movingDist;
        return vector;
    }

    private void createAgents() {
        agents = new ArrayList<>();
        for (int i = 0; i < agentsNumber; i++) {
            agents.add(new RobotAgent((int) start.x, (int) start.y));
        }
    }

    synchronized public void move(RobotAgent agent, Coordinate point) {
        do {
            agent.incUp(MapHolder.INSTANCE.getLevelMap().getLevelUping((int) agent.getPosition().x, (int) agent.getPosition().y, (int) point.x, (int) point.y));
            if (!agent.move(point)) break;
        } while (agent.getPosition().distance(point) > 3);
        agent.getMovedRoute().add(agent.getPosition());
    }


    synchronized public void putPheromone(RobotAgent agent) {
        Coordinate coordinate = agent.getPosition();
        if (pheromone.contains(coordinate)) {
            Double val = pheromone.get(coordinate) + phInit;
            pheromone.put(coordinate, val);
        } else {
            pheromone.put(coordinate, phInit);
        }
    }
}
