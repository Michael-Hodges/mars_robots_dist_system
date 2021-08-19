package model.consensus;

import controller.MessageChannel;
import controller.MessageChannelFactory;
import model.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;

public class ConsensusParticipantImpl implements ConsensusParticipant {

    enum Operation {
        Ping,
        RequestPing
    }

    private final String hostOrIp;
    private final int port;
    private MessageChannelFactory messageChannelFactory;

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
            messageChannel.writeString("consensus");
            messageChannel.writeString(Operation.Ping.name());
            messageChannel.flush();
            String response = messageChannel.readNextString();

            messageChannel.close();
//            Logger.log("Ping response received: " + response);
            return true;
        } catch (IOException e) {
            Logger.log("IOException occurred in ConsensusParticipant or the participant"
                    + unresponsiveParticipant.getHostOrIp()
                    + " "
                    + unresponsiveParticipant.getPort()
                    + " timed out");
            return false;
        }
    }

    @Override
    public boolean requestPing(ConsensusParticipant friend, ConsensusParticipant target) {
        try {
//            Logger.log("CONSENSUS: sending ping from " + friend.getHostOrIp() + " " + friend.getPort()
//                            + " to " + target.getHostOrIp() + " " + target.getPort());
            MessageChannel messageChannel = messageChannelFactory.getChannel(friend.getHostOrIp(), friend.getPort());
            messageChannel.writeString("consensus");
            messageChannel.writeString(Operation.RequestPing.name());
            messageChannel.writeString(target.getHostOrIp());
            messageChannel.writeInt(target.getPort());
            messageChannel.flush();

            String response = messageChannel.readNextString();
            messageChannel.close();
            return response.equals("success");
        } catch (SocketTimeoutException e) {
            Logger.log("CONSENSUS: " + e.getMessage());
            return false;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsensusParticipantImpl that = (ConsensusParticipantImpl) o;
        return port == that.port && Objects.equals(hostOrIp, that.hostOrIp) && Objects.equals(messageChannelFactory, that.messageChannelFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostOrIp, port, messageChannelFactory);
    }

    @Override
    public String toString() {
        return "ConsensusParticipantImpl{" +
                "hostOrIp='" + hostOrIp + '\'' +
                ", port=" + port +
                '}';
    }
}
