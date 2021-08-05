import visualizer.*;

import java.rmi.RemoteException;

public class RemoteSimulationImpl implements RemoteSimulation {

    Simulation s;
    int n = 0;
    public RemoteSimulationImpl(Simulation s) {
        this.s = s;
    }

    @Override
    public RemoteRobot addRobot(int x, int y) throws RemoteException {
        Robot r = new RobotImpl(x, y, 0);
        RemoteRobot rr = new RemoteRobotImpl(r);
        s.addEntity(r);
        RMIRegistry.register(rr, "r" + n);
        n++;
        return rr;
    }


}
