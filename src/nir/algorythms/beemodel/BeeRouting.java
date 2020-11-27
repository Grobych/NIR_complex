package nir.algorythms.beemodel;

import nir.algorythms.BaseRouting;
import nir.algorythms.antmodel.RobotAgent;
import nir.list.ObstacleList;
import nir.model.Route;
import nir.model.global.GlobalVariables;
import nir.model.global.Variable;
import nir.util.Intersection;
import nir.util.RouteUtil;
import nir.util.SortByRouteLenght;
import nir.util.logging.Log;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BeeRouting extends BaseRouting {

    private List<Route> routes = new ArrayList<>();

    public BeeRouting(Coordinate start, Coordinate end, List<Variable> variableList){
        super(start, end, variableList);
    }

    public Route getRoute(Coordinate start, Coordinate end){
        Log.info("Generate route from "+ start + "to " + end);
        routes.clear();

        int agentNumber = params.get("agentNumber").intValue();
        int beeNumber = params.get("beeNumber").intValue();
        int iterations = params.get("iterations").intValue();

        generateBees(agentNumber, beeNumber);

        for (int i = 0; i < iterations; i++) {
            List<Route> randRoutes = generateRandomRoutes();
            routes.addAll(randRoutes);
            Collections.sort(routes, new SortByRouteLenght());
            List<Route> bestRoutes = new ArrayList<>();
            for (int j = 0; j < agentNumber; j++) {
                bestRoutes.add(routes.get(j));
            }
            for (Bee bee : BeeList.bees) {
                Route route = chooseRoute(bestRoutes);
                bee.moveRoute(route);
                route = RouteUtil.bestRoute(route, new Route(bee.getMovedRoute()));
                routes.add(route);
            }
        }

        Log.info("Done");
        Collections.sort(routes, new SortByRouteLenght());
        return routes.get(0);
    }

    private void moveRoute(Bee bee, Route route) {

    }

    private synchronized Route chooseRoute(List<Route> routes) {
        List<Pair<Route, Double>> pairs = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            pairs.add(new Pair<>(routes.get(i),((routes.size() + 1 - i) * Math.E)));
        }
        EnumeratedDistribution enumeratedDistribution = new EnumeratedDistribution<Route>(pairs);
        Route res = (Route) enumeratedDistribution.sample();
        return res;
    }

    private List<Route> generateRandomRoutes() {
        List<Route> res = new ArrayList<>();
        for (RobotAgent agent : BeeList.agents) {
            res.add(randRoute(agent, this.start, this.end));
            System.out.println(agent + "done");
        }
        return res;
    }

    private Route randRoute(RobotAgent agent, Coordinate start, Coordinate end) {
        agent.redirect(start);
        agent.clearMovedRoute();
        int i = 0;
        double dist = GlobalVariables.getInstance().movingDist;
        while (agent.getPosition().distance(end) > GlobalVariables.getInstance().movingDist){
            Coordinate newPos;
            i++;
//            System.out.println(i);
            if (i > 500) {
                agent.setPosition(start);
                agent.clearMovedRoute();
                i = 0;
            }
            do {
                newPos = new Coordinate(agent.getPosition().x + new Random().nextDouble() * 2 * dist -  dist,
                        agent.getPosition().y + new Random().nextDouble() * 2 * dist - dist);
            } while (Intersection.isIntersect(newPos, ObstacleList.getObstacles()));
            if(!agent.move(newPos)) continue;
            agent.getMovedRoute().add(agent.getPosition()); // TODO: ???
        }
        return new Route(agent.getMovedRoute());
    }


    @Override
    public Route call() throws Exception {
        return getRoute(start,end);
    }

    private void generateBees(int agentNumber, int beeNumber){
        BeeList.clearAll();
        for (int i = 0; i < agentNumber; i++) {
            BeeList.addAgent(new RobotAgent(start));
        }
        for (int i = 0; i < beeNumber; i++) {
            BeeList.addBee(new Bee(start));
        }
    }
}
