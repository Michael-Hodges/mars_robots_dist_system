package model;

import controller.TCPMessageEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BullyProcessDelegateImpl implements ProcessDelegate{

    BullyAlgorithmParticipant self;
    ActionListener listener;

    public BullyProcessDelegateImpl(int port) {
        //We use port as processId since it's coming from the coordinator and guaranteed
        //to be sequential and unique.
        self = new BullyAlgorithmParticipantImpl("localhost", port, port);
    }

    public void addParticipant(Peer peer) {
        self.add(toParticipant(peer));
    }

    BullyAlgorithmParticipant toParticipant(Peer peer) {
        return new BullyAlgorithmParticipantImpl(peer.getHostOrIp(), peer.getPort(), peer.getPort());
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
        this.self.setListener(listener);
    }

    public void startElection() {
        this.self.startElection();
    }


    @Override
    public ActionListener onConnection() {
        return new BullyProcess(self);
    }


    class BullyProcess implements ActionListener {

        BullyAlgorithmParticipant self;
        BullyProcess(BullyAlgorithmParticipant self) {
            this.self = self;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            log("Received: " + e.getActionCommand());
            TCPMessageEvent event = (TCPMessageEvent)e;
            try {
                delegate(event);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        private void delegate(TCPMessageEvent event) throws IOException {
            SocketChannel channel = event.getChannel();
            BullyAlgorithmParticipantImpl.Message messageType = BullyAlgorithmParticipantImpl.Message.valueOf(event.getActionCommand());

            int senderProcessId = 0;
            try {
                senderProcessId = channel.in.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }

            switch(messageType) {
                case Election:
                    self.onElectionMessage(senderProcessId);
                    break;
                case Answer:
                    self.onAnswerMessage(senderProcessId);
                    break;
                case Victory:
                    self.onVictoryMessage(senderProcessId);
                    break;
                default:
                    self.startElection();
            }
        }

        void log(String message) {
            Logger.log(message);
        }
    }
}
