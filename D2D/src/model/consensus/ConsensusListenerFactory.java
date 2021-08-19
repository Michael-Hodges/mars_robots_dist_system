package model.consensus;

import controller.MessageChannel;
import controller.MessageEvent;
import controller.MessageListenerFactory;
import view.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ConsensusListenerFactory implements MessageListenerFactory {
    private final ConsensusParticipant consensusParticipant;

    public ConsensusListenerFactory(ConsensusParticipant consensusParticipant) {
        this.consensusParticipant = consensusParticipant;
    }

    @Override
    public ActionListener getMessageListener() {
        return new ConsensusActionListener();
    }

    class ConsensusActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            MessageEvent event = (MessageEvent)e;
            MessageChannel channel = event.getChannel();
            ConsensusParticipantImpl.Operation m = ConsensusParticipantImpl.Operation.valueOf(event.getActionCommand());
            switch(m) {
                case Ping:
                    onPing(channel);
                    break;
                case RequestPing:
                    onRequestPing(channel);
                    break;
            }
        }

        private void onRequestPing(MessageChannel channel) {
            try {
                String hostOrIp = channel.readNextString();
                int port = channel.readNextInt();
                boolean pingResponse = consensusParticipant.ping(new ConsensusParticipantImpl(hostOrIp, port));

                channel.writeString(pingResponse ? "success" : "failure");
                channel.close();
            } catch (IOException e) {
                Logger.log("IOException in ConsensusActionListener: onRequestPing");
            }
        }

        private void onPing(MessageChannel channel) {
            try {
                channel.writeString("success");
                channel.close();
            } catch (IOException e) {
                Logger.log("IOException in ConsensusActionListener: onPing");
            }
        }
    }
}
