package view.gui;

import view.RMIRegistry;

import java.rmi.RemoteException;

public class RemoteDashboardImpl implements RemoteDashboard {

    Dashboard s;
    int n = 0;
    public RemoteDashboardImpl(Dashboard s) {
        this.s = s;
    }

    @Override
    public RemoteRobot addRobot(String label) throws RemoteException {
        view.gui.Robot r = (view.gui.Robot)new view.gui.RobotImpl(label, 0, 0);
        RemoteRobot rr = new RemoteRobotImpl(r);
        s.addEntity(r);
        RMIRegistry.register(rr, label);
        n++;
        return rr;
    }
}
