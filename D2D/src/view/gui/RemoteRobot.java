package view.gui;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI object access for adding a peer to a dashboard
 */
public interface RemoteRobot extends Remote {
    /**
     * Sets the status of the robot
     * @param status status to set for the robot
     * @throws RemoteException java RMI exception
     */
    void setStatus(RobotStatus status) throws RemoteException;

    /**
     * Adds a peer to the Robot
     * @param label label of the peer
     * @throws RemoteException java RMI exception
     */
    void addPeer(String label) throws RemoteException;

    /**
     * Clears peers from robot
     * @throws RemoteException java RMI exception
     */
    void clearPeers() throws RemoteException;

    /**
     * Update the status of a peer
     * @param label label of peer to update
     * @param status status to give to that peer
     * @throws RemoteException java RMI exception
     */
    void updatePeerStatus(String label, RobotStatus status) throws RemoteException;
}
