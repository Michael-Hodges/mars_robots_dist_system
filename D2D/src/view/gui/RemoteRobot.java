package view.gui;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteRobot extends Remote {
    void setStatus(RobotStatus status) throws RemoteException;
    void addPeer(String label) throws RemoteException;
    void clearPeers() throws RemoteException;
    void updatePeerStatus(String label, RobotStatus status) throws RemoteException;
}
