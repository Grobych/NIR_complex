package nir.model.list;

import nir.model.base.Robot;

import java.util.ArrayList;
import java.util.List;

public class RobotList {
    private static List<Robot> robotList = new ArrayList<>();

    public static void add(Robot robot) {
        if (!robotList.contains(robot)) robotList.add(robot);
    }

    public static Robot get(int i) {
        return robotList.get(i);
    }

    public static void clear() {
        robotList.clear();
    }

    public static List<Robot> getRobotList() {
        return robotList;
    }
}
