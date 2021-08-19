package model.consensus;

import controller.MessageChannel;
import controller.MessageChannelFactory;
import model.Logger;

import java.io.IOException;

public class ConsensusParticipantImpl implements ConsensusParticipant {

    enum Operation {
        Ping,
        RequestPing
    }

    private final String hostOrIp;
    private final int port;
    private MessageChannelFactory messageChannelFactory;
    private final String CONSENSUS = "consensus";

    public ConsensusParticipantImpl(String hostOrIp, int port, MessageChannelFactory messageChannelFactory) {
        this.hostOrIp = hostOrIp;
        this.port = port;
        this.messageChannelFactory = messageChannelFactory;
    }

    public ConsensusParticipantImpl(String hostOrIp, int port) {
        this.hostOrIp = hostOrIp;
        this.port = port;
    }

    @Override
    public boolean ping(ConsensusParticipant unresponsiveParticipant) {
        try {
            MessageChannel messageChannel = messageChannelFactory.getChannel(unresponsiveParticipant.getHostOrIp(), unresponsiveParticipant.getPort());
            messageChannel.writeString(this.CONSENSUS);
            messageChannel.writeString(Operation.Ping.name());

            String response = messageChannel.readNextString();
            messageChannel.close();
            Logger.log("Ping response received: " + response);
            return true;
        } catch (IOException e) {
            Logger.log("IOException occurred in ConsensusParticipant");
            return false;
        }
    }

    @Override
    public boolean requestPing(ConsensusParticipant friend, ConsensusParticipant target) {
        try {
            MessageChannel messageChannel = messageChannelFactory.getChannel(friend.getHostOrIp(), friend.getPort());
            messageChannel.writeString(this.CONSENSUS);
            messageChannel.writeString(Operation.RequestPing.name());
            messageChannel.writeString(target.getHostOrIp());
            messageChannel.writeInt(target.getPort());

            String response = messageChannel.readNextString();
            messageChannel.close();
            Logger.log("Ping request response received: " + response);

            return response.equals("success");
        } catch (IOException e) {
            Logger.log("IOException occurred in ConsensusParticipant");
            return false;
        }

    }

    public String getHostOrIp() {
        return hostOrIp;
    }

    public int getPort() {
        return port;
    }

}
