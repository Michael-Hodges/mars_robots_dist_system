package model.bully;

import controller.MessageEvent;
import model.Logger;
import controller.MessageListenerFactory;
import controller.MessageChannel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class BullyMessageListenerFactoryImpl implements MessageListenerFactory {

    BullyAlgorithmParticipant self;
    ActionListener listener;

    public BullyMessageListenerFactoryImpl(BullyAlgorithmParticipant self) {
        //We use port as processId since it's coming from the coordinator and guaranteed
        //to be sequential and unique.
        this.self = self;
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
        this.self.setListener(listener);
    }

    @Override
    public ActionListener getMessageListener() {
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
            MessageEvent event = (MessageEvent)e;
            try {
                delegate(event);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        private void delegate(MessageEvent event) throws IOException {
            MessageChannel channel = event.getChannel();
            BullyAlgorithmParticipantImpl.Message messageType = BullyAlgorithmParticipantImpl.Message.valueOf(event.getActionCommand());

            int senderProcessId = 0;
            try {
                senderProcessId = channel.readNextInt();
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
