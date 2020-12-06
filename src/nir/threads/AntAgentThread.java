package nir.threads;

import nir.algorithms.antmodel.AgentChecker;
import nir.algorithms.antmodel.AntRouting;
import nir.algorithms.antmodel.RobotAgent;
import nir.model.Route;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public class AntAgentThread implements Runnable {
    private RobotAgent agent;
    private AntRouting routing;

    public void setAgent(RobotAgent agent) {
        this.agent = agent;
    }

    public void setRouting(AntRouting routing) {
        this.routing = routing;
    }


    @Override
    public void run() {
        do {
            Coordinate point = routing.getCoordinate(agent, routing.getGoal());
            routing.move(agent, point);
            if (routing.goalTaken(agent)) {
                agent.setGoalTaken(true);
                List<Coordinate> list = agent.getMovedRoute();
                Route route = new Route(list);
                routing.routeList.add(route);
                AgentChecker.check();
                break;
            }
            if (routing.agentMoveTooLong(agent)) {
                AgentChecker.check();
                break;
            }
        } while (true);
    }
}
