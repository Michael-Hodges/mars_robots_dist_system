import visualizer.*;

public class Server {

    public void start() {
        SimulationImpl s = new SimulationImpl();
        RemoteSimulation rs = new RemoteSimulationImpl(s);
        //Robot r = new RobotImpl(500,500,0);
        //s.addEntity(r);
        //RemoteRobot stub = new RemoteRobotImpl(r);
        //RMIRegistry.register(stub, "robot_1");
        RMIRegistry.register(rs, "simulation");
        VisualizationPanel panel = new VisualizationPanel();
        panel.start(s);
    }
}
