package model.sim;

import controller.MessageChannel;
import controller.MessageChannelFactory;

import java.io.IOException;

/**
 * Client for running chaos between the peers.
 */
public class ChaosClient {

    MessageChannelFactory messageFactory;

    /**
     * Creates a new Chaos client with a given MessageChannelFactory
     * @param messageFactory messageFactory to use
     */
    public ChaosClient(MessageChannelFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * Blocks a given route to simulate a peer going down.
     * @param hostOrIp host/ip of peer to block
     * @param port port of peer to block
     * @param route route of given peer to block
     */
    void blockRoute(String hostOrIp, int port, String route) {
        sendOperation(hostOrIp, port, ChaosOperation.Block, route);
    }

    /**
     * Unblocks a given route to simulate a peer going down.
     * @param hostOrIp host/ip of peer to unblock
     * @param port port of peer to unblock
     * @param route route of given peer to unblock
     */
    void unblockRoute(String hostOrIp, int port, String route) {
        sendOperation(hostOrIp, port, ChaosOperation.Unblock, route);
    }

    /**
     * Sends operation on a new channel to the peer.
     * @param hostOrIp host/ip to send message to
     * @param port port to send message to
     * @param operation name of operation (block or unblock)
     * @param route which route will be blocked or unblocked
     */
    void sendOperation(String hostOrIp, int port, ChaosOperation operation, String route) {
        try {
            MessageChannel channel = messageFactory.getChannel(hostOrIp, port);
            channel.writeString("chaos");
            channel.writeString(operation.toString());
            channel.writeString(route);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
