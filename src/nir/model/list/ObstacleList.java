package nir.model.list;

import nir.model.base.Obstacle;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class ObstacleList {
    private static List<Obstacle> obstacles = new ArrayList<>();

    public static void add(Obstacle obstacle){
        obstacles.add(obstacle);
    }

    public static List<Obstacle> getObstacles(){
        return obstacles;
    }

    public static void init(){
        obstacles.clear();
        int[][] o1 = {{100, 50}, {300, 50}, {300, 100}, {100, 100}};
        int[][] o2 = {{200, 400}, {220, 400}, {220, 430}, {200, 430}};
        int[][] o3 = {{260, 670}, {270, 670}, {270, 690}, {260, 690}};
        int[][] o4 = {{50, 600}, {120, 600}, {120, 620}, {50, 620}};
        createObstacle(o1);
        createObstacle(o2);
        createObstacle(o3);
        createObstacle(o4);
    }

    public static void createObstacle(int[][] c) {
        List<Coordinate> obs = new ArrayList<>();
        for (int i = 0; i < c.length; i++) {
            obs.add(new Coordinate(c[i][0], c[i][1]));
        }
        obs.add(new Coordinate(c[0][0], c[0][1]));
        obstacles.add(new Obstacle(obs.toArray(new Coordinate[obs.size()])));
    }
}
