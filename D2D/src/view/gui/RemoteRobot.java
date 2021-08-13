package view.gui;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteRobot extends Remote {
    void move(int amount) throws RemoteException;
    void rotate(int degrees) throws RemoteException;
    void setColor(int r, int g, int b) throws RemoteException;
    void setLabel(String label) throws RemoteException;
}
