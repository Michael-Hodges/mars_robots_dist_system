package model.bully;

import controller.MessageEvent;
import model.Logger;
import controller.MessageListenerFactory;
import controller.MessageChannel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * MessageListener Factory for the BullyAlgorithm
 */
public class BullyMessageListenerFactoryImpl implements MessageListenerFactory {

    BullyAlgorithmParticipant self;
    ActionListener listener;

    /**
     * Constructs new MessageListenerFactory with self set as given participant.
     * @param self participant to assign the listener to
     */
    public BullyMessageListenerFactoryImpl(BullyAlgorithmParticipant self) {
        //We use port as processId since it's coming from the coordinator and guaranteed
        //to be sequential and unique.
        this.self = self;
    }

    /**
     * Assign the listener to the self participant
     * @param listener listener to assign to the participant
     */
    public void setListener(ActionListener listener) {
        this.listener = listener;
        this.self.setListener(listener);
    }

    @Override
    public ActionListener getMessageListener() {
        return new BullyProcess(self);
    }


    /**
     * ActionListener/MessageListener for the bully algorithm
     */
    class BullyProcess implements ActionListener {

        BullyAlgorithmParticipant self;

        /**
         * Constructs new BullyProcess action listener with a given participant
         * @param self participant which will be taking actions
         */
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

        /**
         * Determines which method to call on the self participant given the event
         * @param event event for the participant to take
         * @throws IOException JAva socket/io errors
         */
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

        /**
         * Logs a string
         * @param message string to log
         */
        void log(String message) {
            Logger.log(message);
        }
    }
}
