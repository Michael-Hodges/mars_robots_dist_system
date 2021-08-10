package view.gui;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteSimulation extends Remote {
    RemoteRobot addRobot(int x, int y) throws RemoteException;
}
