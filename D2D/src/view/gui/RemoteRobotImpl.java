package view.gui;


import java.rmi.RemoteException;

public class RemoteRobotImpl implements RemoteRobot {

    private view.gui.Robot wrappedRobot;

    public RemoteRobotImpl(Robot robot) {
        this.wrappedRobot = robot;
    }

    @Override
    public void setStatus(RobotStatus status) {
        this.wrappedRobot.setStatus(status);
    }

    @Override
    public void addPeer(String label) throws RemoteException {
        this.wrappedRobot.addPeer(label);
    }

    @Override
    public void clearPeers() throws RemoteException {
        this.wrappedRobot.removeAllPeers();
    }


    @Override
    public void updatePeerStatus(String label, RobotStatus status) throws RemoteException {
        this.wrappedRobot.setPeerStatus(label, status);
    }
}
