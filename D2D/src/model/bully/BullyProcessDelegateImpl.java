package model.bully;

import controller.TCPMessageEvent;
import model.Logger;
import model.ProcessDelegate;
import model.SocketChannel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class BullyProcessDelegateImpl implements ProcessDelegate {

    BullyAlgorithmParticipant self;
    ActionListener listener;

    public BullyProcessDelegateImpl(BullyAlgorithmParticipant self) {
        //We use port as processId since it's coming from the coordinator and guaranteed
        //to be sequential and unique.
        this.self = self;
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
        this.self.setListener(listener);
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
            }
        }

        void log(String message) {
            Logger.log(message);
        }
    }
}
