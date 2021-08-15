package model.sim;

import controller.MessageChannel;
import controller.MessageChannelFactory;

import java.io.IOException;


public class ChaosClient {

    MessageChannelFactory messageFactory;

    public ChaosClient(MessageChannelFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    void blockRoute(String hostOrIp, int port, String route) {
        sendOperation(hostOrIp, port, ChaosOperation.Block, route);
    }

    void unblockRoute(String hostOrIp, int port, String route) {
        sendOperation(hostOrIp, port, ChaosOperation.Unblock, route);
    }

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
