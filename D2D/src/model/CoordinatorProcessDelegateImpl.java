package model;

import controller.TCPMessageEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;


public class CoordinatorProcessDelegateImpl implements ProcessDelegate {

    enum Message {
        Register,
        GetNodes
    }

    Coordinator coordinator;
    public CoordinatorProcessDelegateImpl(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public ActionListener onConnection() {
        return new CoordinatorProcess();
    }


    class CoordinatorProcess implements ActionListener {
        SocketChannel channel;

        @Override
        public void actionPerformed(ActionEvent e) {
            TCPMessageEvent event = (TCPMessageEvent)e;
            this.channel = event.getChannel();
            Message message = Message.valueOf(event.getActionCommand());
            try {
                this.handleInput(message);
                this.channel.out.flush();
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
            String hostOrIp = channel.in.readUTF();
            CoordinatorProcessDelegateImpl.this.log("Registering node: " + hostOrIp);
            int port = CoordinatorProcessDelegateImpl.this.coordinator.registerNode(hostOrIp);
            CoordinatorProcessDelegateImpl.this.log("Registering port: " + port);
            channel.out.writeInt(port);
            channel.out.flush();
        }

        private void onGetNodes() throws IOException {
            CoordinatorProcessDelegateImpl.this.log("Retrieving nodes...");
            List<String> nodes = CoordinatorProcessDelegateImpl.this.coordinator.getNodes();
            channel.out.writeInt(nodes.size());
            for (String node : nodes) {
                channel.out.writeUTF(node);
            }
            channel.out.flush();
        }
    }

    void log(String message) {
        Logger.log(message);
    }
}
