package model;

import controller.MessageChannel;
import controller.MessageListenerFactory;
import controller.MessageEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;

/**
 * Implementation of a MessageListenerFactory for use by a coordinator
 */
public class CoordinatorMessageListenerFactoryImpl implements MessageListenerFactory {
    /**
     * Messages a coordinator would use
     */
    enum Message {
        Register,
        GetNodes
    }

    Coordinator coordinator;

    /**
     * Constructs new factory for a given constructor
     * @param coordinator coordinator to use with this factory
     */
    public CoordinatorMessageListenerFactoryImpl(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public ActionListener getMessageListener() {
        return new CoordinatorProcess();
    }

    /**
     * Process used to handle action events for the coordinator
     */
    class CoordinatorProcess implements ActionListener {
        MessageChannel channel;

        @Override
        public void actionPerformed(ActionEvent e) {
            MessageEvent event = (MessageEvent)e;
            this.channel = event.getChannel();
            Message message = Message.valueOf(event.getActionCommand());
            try {
                this.handleInput(message);
                this.channel.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        /**
         * Handles the message input from the ActionEvent
         * @param message message to determine action
         * @throws IOException Java io/socket errors
         */
        private void handleInput(Message message) throws IOException {

            switch(message) {
                case Register:
                    onRegister();
                    break;
                case GetNodes:
                    onGetNodes();
                    break;
                default:
                    Logger.log("Error.");
            }
            channel.close();
        }

        /**
         * Registers node with the coordinator.
         * @throws IOException Java socket/io exceptions
         */
        private void onRegister() throws IOException {
            String hostOrIp = channel.readNextString();
            CoordinatorMessageListenerFactoryImpl.this.log("Registering node: " + hostOrIp);
            int port = CoordinatorMessageListenerFactoryImpl.this.coordinator.registerNode(hostOrIp);
            CoordinatorMessageListenerFactoryImpl.this.log("Registering port: " + port);
            channel.writeInt(port);
            channel.flush();
        }

        /**
         * Returns nodes to the requester
         * @throws IOException Java socket/io exceptions
         */
        private void onGetNodes() throws IOException {
            CoordinatorMessageListenerFactoryImpl.this.log("Retrieving nodes...");
            List<String> nodes = CoordinatorMessageListenerFactoryImpl.this.coordinator.getNodes();
            channel.writeInt(nodes.size());
            for (String node : nodes) {
                channel.writeString(node);
            }
            channel.flush();
        }
    }

    void log(String message) {
        Logger.log(message);
    }
}
