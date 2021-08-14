package view.gui;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteDashboard extends Remote {
    RemoteRobot addRobot(String label) throws RemoteException;
}
