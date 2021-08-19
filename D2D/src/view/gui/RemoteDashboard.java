package view.gui;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Enables RMI access for adding robots
 */
public interface RemoteDashboard extends Remote {
    /**
     * Adds a new robot
     * @param label label to attach to robot
     * @return Returns the robot that has been added
     * @throws RemoteException Java RMI exceptions
     */
    RemoteRobot addRobot(String label) throws RemoteException;
}
