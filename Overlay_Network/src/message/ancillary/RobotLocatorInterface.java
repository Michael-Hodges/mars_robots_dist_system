package message.ancillary;

public interface RobotLocatorInterface extends java.rmi.Remote
{
    void getReachableRobots()
            throws java.rmi.RemoteException;

    void updateLocation()
            throws java.rmi.RemoteException;
}
