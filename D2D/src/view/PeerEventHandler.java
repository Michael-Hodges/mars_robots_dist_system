package view;

import model.Peer;
import model.PeerEvent;
import model.PeerImpl;
import view.gui.RemoteRobot;
import view.gui.RemoteDashboard;
import view.gui.RobotStatus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Random;

public class PeerEventHandler implements ActionListener {
    private static Random random = new Random();
    RemoteRobot robot;
    Peer peer;
    public PeerEventHandler(Peer peer) {
        this.peer = peer;
        setRobotDashboardWidget();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = e.getActionCommand();
        PeerEvent operation = PeerEvent.valueOf(message);
        switch(operation) {
            case BullySendVictory:
                onBecomeLeader();
                break;
            case BullyReceiveVictory:
                onBecomeParticipant();
                break;
            default:
                break;
        }
    }

    private void setRobotDashboardWidget() {
        try {
        RemoteDashboard s = (RemoteDashboard) RMIRegistry.retrieve("localhost", "dashboard");
        String identifier = getIdentifier();
        Logger.log("Registering with dashboard " + identifier);
        this.robot = s.addRobot(identifier);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onBecomeLeader() {
        setRobotStatus(RobotStatus.Leader);
    }

    private void onBecomeParticipant() {
        setRobotStatus(RobotStatus.Up);
    }

    private void setRobotStatus(RobotStatus status) {
        try {
            this.robot.setStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String getIdentifier() {
        return Integer.toString(peer.getPort());
    }

}
