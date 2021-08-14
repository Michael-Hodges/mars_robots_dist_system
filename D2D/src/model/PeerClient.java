package model;

import controller.MessageChannel;
import controller.MessageChannelFactory;

import java.io.IOException;

public class PeerClient {

    MessageChannelFactory messageChannelFactory;

    public PeerClient(MessageChannelFactory messageChannelFactory) {
        this.messageChannelFactory = messageChannelFactory;
    }

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
}
