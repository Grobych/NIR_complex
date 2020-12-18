package nir.model.algorithms.swarmmodel;

import nir.model.algorithms.BaseRouting;
import nir.model.list.ObstacleList;
import nir.model.base.Route;
import nir.model.global.Variable;
import nir.model.map.MapHolder;
import nir.model.util.Intersection;
import nir.model.util.RouteUtil;
import nir.model.util.logging.Log;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static nir.model.util.RouteUtil.calculateRouteLenght;

public class SwarmRouting extends BaseRouting {

    double g = Double.MAX_VALUE;
    RobotParticle gParticle;
    private List<Route> routes = new ArrayList<>();

    public SwarmRouting(Coordinate start, Coordinate end, List<Variable> variableList){
        super(start, end, variableList);
    }

    public Route getRoute(Coordinate start, Coordinate end){
        routes.clear();
        generateAgents(start);
        gParticle = ParticlesList.get(0);
        g = calculateG(gParticle.getPosition(),end);
        setInitSpeed(ParticlesList.list);
        int iterations = params.getInt("steps");
        int i = 0;
        while (true){
            i++;
            if (i > iterations) {
                return null;
            }
            List<Coordinate> coordinates = generateRandomSpeed(ParticlesList.list, end, gParticle);
            if (coordinates == null) return null;
            runAgents(ParticlesList.list, coordinates);
            checkResults(ParticlesList.list, end);
            if (goalTaken(ParticlesList.list, end)) {
                break;
                // TODO !!!
            }
        }
        return new Route(gParticle.getMovedRoute());   //bestRoute(ParticlesList.list);
    }

    private boolean goalTaken(List<RobotParticle> list, Coordinate goal) {
        for (RobotParticle particle : list) {
            if (particle.getPosition().distance(goal) < 5){
                gParticle = particle;
                return true;
            }
        }
        return false;
    }

    private Route bestRoute(List<RobotParticle> list) {
        double min = Double.MAX_VALUE;
        Route best = new Route();
        for (RobotParticle particle : list) {
            double f = calculateRouteLenght(particle.getMovedRoute());
            if (f < min) {
                best =  new Route(particle.getMovedRoute());
                min = f;
            }
        }
        return best;
    }

    private void checkResults(List<RobotParticle> list, Coordinate end) {
        for (RobotParticle particle : list) {
            double f = calculateG(particle.getPosition(),end);
            if (f < g){
                g = f;
                gParticle = particle;
            }
        }
    }

    private void runAgents(List<RobotParticle> list, List<Coordinate> coordinates) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).move(coordinates.get(i));
        }
    }

    private List<Coordinate> generateRandomSpeed(List<RobotParticle> list, Coordinate goal, RobotParticle gParticle) {
        List<Coordinate> res = new ArrayList<>();
        for (RobotParticle particle : list) {
            Vector2D speed;
            Coordinate c;
            int i = 0;
            do{
                i++;
                if (i > 200) return null;
                if (particle.getPosition() == gParticle.getPosition()) speed = getVector2DRand(particle.getPosition(),goal);
                else speed = getVector2DRand(particle.getPosition(),gParticle.getPosition());
                double x = particle.getPosition().x;
                double y = particle.getPosition().y;
                c = new Coordinate(x+speed.getX(), y + speed.getY());
            } while (Intersection.isIntersect(c, ObstacleList.getObstacles()));
            res.add(c);
        }
        return res;
    }

    private Vector2D getVector2DRand(Coordinate from, Coordinate to){
        double movingDist = params.get("movingDist");
        double v1 = (to.x - from.x) / from.distance(to) * movingDist ;
        double v2 = (to.y - from.y) / from.distance(to) * movingDist ;
        Vector2D toG = new Vector2D(v1,v2);

        double r1 = new Random().nextInt((int)movingDist * 2) - movingDist;
        double r2 = new Random().nextInt((int)movingDist * 2) - movingDist;
        Vector2D toR = new Vector2D(r1,r2);

        double gCoef = params.get("toGCoef");
        double rCoef = params.get("toRCoef");
        return new Vector2D(toG.getX() * gCoef + toR.getX() * rCoef, toG.getY() * gCoef + toR.getY() * rCoef);
    }

    private void setInitSpeed(List<RobotParticle> list) {
        for (RobotParticle particle : list) {
            setRandomSpeed(particle);
        }
    }

    private void setRandomSpeed(RobotParticle particle) {
        particle.setRandomSpeed();
    }

    private double calculateG(Coordinate c1, Coordinate c2) {
        double res = 0d;
        res += c1.distance(c2);
        res += MapHolder.INSTANCE.getLevelMap().getLineLevelUpping(c1, c2) * 6;
        return res;
    }

    private void generateAgents(Coordinate start) {
        int n = params.getInt("agentsNumber");
        ParticlesList.clear();
        for (int i = 0; i < n; i++) {
            ParticlesList.add(new RobotParticle(start));
        }
    }

    @Override
    public Route call() throws Exception {
        Route best = new Route();
        Log.info("Generate route from "+ start + "to " + end);
        for (int i = 0; i < params.getInt("iterations"); i++) {
            Log.info("Iteration "+ i);
            Route route = getRoute(start,end);
            if (route == null) {
                System.out.println("null");
                i--;
                continue;
            }
            if (i>0) {
                best = RouteUtil.bestRoute(best,route);
            } else {
                best = route;
            }
        }
        Route res = new Route();
        res.add(start);
        res.addRoute(best);
        res.add(end);
        Log.info("Done");
        return best;
    }

}
