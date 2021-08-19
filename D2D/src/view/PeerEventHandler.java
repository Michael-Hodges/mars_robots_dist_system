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

/**
 * ActionListener to handle Peer events
 */
public class PeerEventHandler implements ActionListener {
    private static Random random = new Random();
    RemoteRobot robot;
    Peer peer;

    /**
     * Constructs a new handler for a given peer
     * @param peer peer to handle events for
     */
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

    /**
     * Sets the remote dashboard for this EventHandler
     */
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

    /**
     * Sets the robot status to be leader
     */
    private void onBecomeLeader() {
        setRobotStatus(RobotStatus.Leader);
    }

    /**
     * Updates the dashboard by refreshing self and peers
     */
    private void updateDashboard() {
        try {
            refreshSelf();
            refreshPeers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes self with updated status, and updates robot status
     * @throws RemoteException Java RMI exceptions
     */
    private synchronized void refreshSelf() throws RemoteException {
        PeerImpl.Status status = peer.getStatus();
        this.robot.setStatus(toRobotStatus(status));
    }

    /**
     * Refresh the status of the peers and sends those to robots
     * @throws RemoteException Java RMI exception
     */
    private synchronized void refreshPeers() throws RemoteException {
        this.robot.clearPeers();
        for(Peer p : peer.getPeers()) {
            String peerId = getIdentifier(p);
            this.robot.addPeer(peerId);
            this.robot.updatePeerStatus(peerId, toRobotStatus(p.getStatus()));
        }
    }

    /**
     * Convert a PeerStatus to a RobotStatus
     * @param status peer status to convert
     * @return the robot status version of the peer status
     */
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


    /**
     * Set the robot status to up
     */
    private void onBecomeParticipant() {
        setRobotStatus(RobotStatus.Up);
    }

    /**
     * Sets robot status to a given status
     * @param status status to set for the robot
     */
    private void setRobotStatus(RobotStatus status) {
        try {
            this.robot.setStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get identifier of the peer
     * @param peer peer to get identifier of
     * @return the port number of the peer
     */
    private String getIdentifier(Peer peer) {
        return Integer.toString(peer.getPort());
    }

}
