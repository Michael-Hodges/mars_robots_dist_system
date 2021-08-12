package model;

import controller.MessageChannel;
import controller.MessageListenerFactory;
import controller.MessageEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;


public class CoordinatorMessageListenerFactoryImpl implements MessageListenerFactory {

    enum Message {
        Register,
        GetNodes
    }

    Coordinator coordinator;
    public CoordinatorMessageListenerFactoryImpl(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public ActionListener getMessageListener() {
        return new CoordinatorProcess();
    }


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

        private void onRegister() throws IOException {
            String hostOrIp = channel.readNextString();
            CoordinatorMessageListenerFactoryImpl.this.log("Registering node: " + hostOrIp);
            int port = CoordinatorMessageListenerFactoryImpl.this.coordinator.registerNode(hostOrIp);
            CoordinatorMessageListenerFactoryImpl.this.log("Registering port: " + port);
            channel.writeInt(port);
            channel.flush();
        }

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
