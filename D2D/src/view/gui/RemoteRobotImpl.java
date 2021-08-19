package view.gui;


import java.rmi.RemoteException;

/**
 * Implementation of the Remote Robot, a wrap of a normal Robot
 */
public class RemoteRobotImpl implements RemoteRobot {

    private view.gui.Robot wrappedRobot;

    /**
     * Constructs a new RemoteRobot
     * @param robot robot to be wrapped
     */
    public RemoteRobotImpl(Robot robot) {
        this.wrappedRobot = robot;
    }

    @Override
    public synchronized void setStatus(RobotStatus status) {
        this.wrappedRobot.setStatus(status);
    }

    @Override
    public synchronized void addPeer(String label) throws RemoteException {
        this.wrappedRobot.addPeer(label);
    }

    @Override
    public synchronized void clearPeers() throws RemoteException {
        this.wrappedRobot.removeAllPeers();
    }


    @Override
    public synchronized void updatePeerStatus(String label, RobotStatus status) throws RemoteException {
        this.wrappedRobot.setPeerStatus(label, status);
    }
}
