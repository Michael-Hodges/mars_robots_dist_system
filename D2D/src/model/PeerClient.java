package model;

import controller.MessageChannel;
import controller.MessageChannelFactory;

import java.io.IOException;

/**
 * Peer Client for use by the driver program.
 */
public class PeerClient {

    MessageChannelFactory messageChannelFactory;

    /**
     * Creates a new peer client.
     * @param messageChannelFactory messageChannelFactory to use for this client
     */
    public PeerClient(MessageChannelFactory messageChannelFactory) {
        this.messageChannelFactory = messageChannelFactory;
    }

    /**
     * Sends a message to a peer to start a leader election
     * @param hostOrIp host to send message to
     * @param port port to send message to
     */
    public void startLeaderElection(String hostOrIp, int port) {
        try {
            MessageChannel channel = messageChannelFactory.getChannel(hostOrIp, port);
            channel.writeString("peer");
            channel.writeString(PeerImpl.Operation.ElectLeader.toString());
            channel.writeInt(0);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to a peer to discover the local group
     * @param hostOrIp host to send message to
     * @param port port to send message to
     */
    public void discoverLocalGroup(String hostOrIp, int port) {
        try {
            MessageChannel channel = messageChannelFactory.getChannel(hostOrIp, port);
            channel.writeString("peer");
            channel.writeString(PeerImpl.Operation.IdentifyLocalGroup.toString());
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to a peer to relocate itself to the given coordinates
     * @param hostOrIp host to send message to
     * @param port port to send message to
     * @param x x coordinate to relocate to
     * @param y y coordinate to relocate to
     */
    public void relocate(String hostOrIp, int port, int x, int y) {
        try {
            MessageChannel channel = messageChannelFactory.getChannel(hostOrIp, port);
            channel.writeString("peer");
            channel.writeString(PeerImpl.Operation.UpdatePosition.toString());
            channel.writeInt(x);
            channel.writeInt(y);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to a peer to start a multicast relocate of all nodes to given coordinates
     * @param hostOrIp host to send message to
     * @param port port to send message to
     * @param x x coordinate to relocate to
     * @param y y coordinate to relocate to
     */
    public void multicastRelocate(String hostOrIp, int port, int x, int y) {
        try {
            MessageChannel channel = messageChannelFactory.getChannel(hostOrIp, port);
            channel.writeString("peer");
            channel.writeString(PeerImpl.Operation.InitiateMulticastUpdatePosition.name());
            channel.writeInt(x);
            channel.writeInt(y);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
