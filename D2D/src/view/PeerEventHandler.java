package view;

import model.Peer;
import model.PeerEvent;
import model.PeerImpl;
import view.gui.RemoteRobot;
import view.gui.RemoteDashboard;
import view.gui.RobotStatus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
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
            case PeerAdded:
            case PeerRemoved:
            case PeerStatusUpdated:
                updateDashboard();
                break;
            default:
                break;
        }
    }

    private void setRobotDashboardWidget() {
        try {
        RemoteDashboard s = (RemoteDashboard) RMIRegistry.retrieve("localhost", "dashboard");
        String identifier = getIdentifier(this.peer);
        Logger.log("Registering with dashboard " + identifier);
        this.robot = s.addRobot(identifier);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onBecomeLeader() {
        setRobotStatus(RobotStatus.Leader);
    }

    private void updateDashboard() {
        try {
            refreshSelf();
            refreshPeers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private synchronized void refreshSelf() throws RemoteException {
        PeerImpl.Status status = peer.getStatus();
        this.robot.setStatus(toRobotStatus(status));
    }

    private synchronized void refreshPeers() throws RemoteException {
        this.robot.clearPeers();
        for(Peer p : peer.getPeers()) {
            String peerId = getIdentifier(p);
            this.robot.addPeer(peerId);
            this.robot.updatePeerStatus(peerId, toRobotStatus(p.getStatus()));
        }
    }

    RobotStatus toRobotStatus(PeerImpl.Status status) {
        switch(status) {
            case Up:
                return RobotStatus.Up;
            case Down:
                return RobotStatus.Down;
            case Leader:
                return RobotStatus.Leader;
            default:
                return RobotStatus.Unknown;
        }
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

    private String getIdentifier(Peer peer) {
        return Integer.toString(peer.getPort());
    }

}
