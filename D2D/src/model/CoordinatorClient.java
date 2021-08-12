package model;

import controller.MessageChannel;
import controller.tcp.TCPMessageChannelImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CoordinatorClient implements Coordinator{

    String hostOrIp;
    int port;
    public CoordinatorClient(String hostOrIp, int port) {
        this.hostOrIp = hostOrIp;
        this.port = port;
    }

    @Override
    public int registerNode(String nodeName) {
        PortRequest rq = new PortRequest(nodeName);
        this.sendReceive(c -> rq.accept(c));
        if (rq.isSuccess) {
            log("Port received: " + rq.port);
            return rq.port;
        }
        else {
            log("Unable to receive port.");
        }
        return -1;
    }

    @Override
    public List<String> getNodes() {
        HostListRequest rq = new HostListRequest();
        this.sendReceive(c -> rq.accept(c));
        if (rq.isSuccess) {
            log("Hosts received.");
            return rq.hosts;
        } else {
            log("Unable to receive hosts.");
            return new ArrayList<>();
        }
    }



    private class PortRequest {

        int port;
        boolean isSuccess;
        String nodeName;
        public PortRequest(String nodeName) {
            this.nodeName = nodeName;
        }

        public void accept(MessageChannel channel) {
            this.isSuccess = false;
            this.port = -1;
            try {
                log("Registering: " + nodeName);
                channel.writeString("coordinator");
                channel.writeString(CoordinatorMessageListenerFactoryImpl.Message.Register.name());
                channel.writeString(nodeName);
                channel.flush();
                this.port = channel.readNextInt();
                this.isSuccess = true;
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class HostListRequest {
        boolean isSuccess;
        List<String> hosts;
        public HostListRequest() {
        }

        public void accept(MessageChannel channel) {
            this.isSuccess = false;
            this.hosts = new ArrayList<>();
            try {
                log("Retrieving hosts...");
                channel.writeString("coordinator");
                channel.writeString(CoordinatorMessageListenerFactoryImpl.Message.GetNodes.name());
                channel.flush();

                int nodeCount = channel.readNextInt();
                while(nodeCount > 0) {
                    String host = channel.readNextString();
                    log("Received - " + host);
                    hosts.add(host);
                    nodeCount--;
                }
                this.isSuccess = true;

                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendReceive(Consumer<MessageChannel> onConnection) {
        try {
            MessageChannel channel = new TCPMessageChannelImpl(this.hostOrIp, this.port);
            onConnection.accept(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void log(String msg) {
        Logger.log(msg);
    }
}
