package nir.algorithms.antmodel;

import org.locationtech.jts.geom.Coordinate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class Pheromone {
    private HashMap<Coordinate, Double> pheromoneMap = new HashMap();
    public Semaphore phMutex = new Semaphore(1);


    public Set<Coordinate> key(){return pheromoneMap.keySet();}

    public void put(Coordinate c, Double val) {
        pheromoneMap.put(c,val);
    }
    public double get(Coordinate c){
        return pheromoneMap.get(c);
    }
    public boolean contains(Coordinate coordinate){
        return pheromoneMap.containsKey(coordinate);
    }
    public void expand(Coordinate c, Double val){
        if (pheromoneMap.keySet().contains(c)){
            double old = pheromoneMap.get(c);
            put(c,old + val);
        } else {
            put(c,val);
        }
    }
    public void clear(){
        pheromoneMap.clear();
        pheromoneMap = new HashMap<>();
    }

    synchronized public boolean unitePheromone() throws InterruptedException {
        boolean res = false;
//        System.out.println("locked");
        phMutex.acquire();
        Coordinate[] keys = pheromoneMap.keySet().toArray(new Coordinate[pheromoneMap.keySet().size()]);
        for (int i = 0; i < keys.length - 1; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                if (keys[i].distance(keys[j]) < 5 && pheromoneMap.get(keys[i]) != 0 && pheromoneMap.get(keys[j]) != 0) {
                    res = true;
                    Coordinate n = new Coordinate((keys[i].x + keys[j].x) / 2, (keys[i].y + keys[j].y) / 2);
                    Double v = pheromoneMap.get(keys[i]) + pheromoneMap.get(keys[j]);
                    pheromoneMap.put(n, v);
                    pheromoneMap.replace(keys[i], 0d);
                    pheromoneMap.replace(keys[j], 0d);
                }
            }
        }
        Iterator<Coordinate> iterator = pheromoneMap.keySet().iterator();
        while (iterator.hasNext()) {
            Coordinate c = iterator.next();
            if (pheromoneMap.get(c) == 0) iterator.remove();
        }
        phMutex.release();
//        System.out.println("united");
//        System.out.println("released");
        return res;
    }


    public void fullUnitePheromone() throws InterruptedException {
        int i = 0;
        double sum = 0;
        for (Coordinate c : pheromoneMap.keySet()) {
            sum += pheromoneMap.get(c);
        }
        sum = sum / pheromoneMap.size() * 1.80;

        Iterator<Coordinate> iterator = pheromoneMap.keySet().iterator();
        while (iterator.hasNext()) {
            Coordinate c = iterator.next();
            if (pheromoneMap.get(c) < sum) iterator.remove();
        }
        while (unitePheromone()) {
            System.out.println(i++);
        }

    }


    private void showPh() {
        for (Coordinate c : pheromoneMap.keySet()) {
            System.out.print(c + " " + pheromoneMap.get(c) + ";  ");
        }
        System.out.println();
    }

    synchronized public void expandPheromone(RobotAgent agent, Coordinate start, double phEx) {
        Double routeLength = AntRouting.calculateRouteLenght(agent);
//        System.out.println("expanding " + agent.getMovedRoute().size());
        try {
            phMutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Coordinate coordinate : agent.getMovedRoute()) {
            if (coordinate.distance(start) < 20) continue;
            try {
                if (contains(coordinate)) put(coordinate, get(coordinate) + phEx / routeLength);
                else put(coordinate,phEx / routeLength);
            } catch (NullPointerException e) {
                System.out.println(start);
                System.out.println(coordinate);
                System.out.println(pheromoneMap.containsKey(coordinate));
            } finally {
                continue;
            }
        }
        phMutex.release();
    }

    synchronized public void evaporatePheromone(double phThreshold) throws InterruptedException {
        phMutex.acquire();
//        System.out.println("locked");
        Iterator<Coordinate> iterator = pheromoneMap.keySet().iterator();
        while (iterator.hasNext()) {
            Coordinate coordinate = iterator.next();
            Double val = pheromoneMap.get(coordinate);
            if (val > phThreshold) pheromoneMap.replace(coordinate, val * 0.9);
            else iterator.remove();
        }
        phMutex.release();
//        System.out.println(pheromoneMap.size());
//        System.out.println("unlocked");
    }

}
