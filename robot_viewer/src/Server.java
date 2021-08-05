import visualizer.*;

public class Server {

    public void start() {
        SimulationImpl s = new SimulationImpl();
        Robot r = new RobotImpl(500,500,0);
        s.addEntity(r);
        RemoteRobot stub = new RemoteRobotImpl(r);
        RMIRegistry.register(stub, "robot_1");
        VisualizationPanel panel = new VisualizationPanel();
        panel.start(s);
    }
}
