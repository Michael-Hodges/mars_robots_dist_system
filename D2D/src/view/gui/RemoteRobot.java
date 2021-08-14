package view.gui;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteRobot extends Remote {
    void setStatus(RobotStatus status) throws RemoteException;
}
