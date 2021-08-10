package view.gui;

import view.RMIRegistry;
import view.gui.*;

import java.rmi.RemoteException;

public class RemoteSimulationImpl implements RemoteSimulation {

    Simulation s;
    int n = 0;
    public RemoteSimulationImpl(Simulation s) {
        this.s = s;
    }

    @Override
    public RemoteRobot addRobot(int x, int y) throws RemoteException {
        view.gui.Robot r = (view.gui.Robot)new view.gui.RobotImpl(x, y, 0);
        RemoteRobot rr = new RemoteRobotImpl(r);
        s.addEntity(r);
        RMIRegistry.register(rr, "r" + n);
        n++;
        return rr;
    }


}
