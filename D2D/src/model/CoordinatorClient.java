package model;

import controller.MessageChannel;
import controller.tcp.TCPMessageChannelImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Client class of a coordinator which nodes will register with.
 */
public class CoordinatorClient implements Coordinator{

    String hostOrIp;
    int port;

    /**
     * Constructs a new client
     * @param hostOrIp Host/ip to listen on
     * @param port port to listen on
     */
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


    /**
     * Port request class, used to accept a message channel to register a node.
     */
    private class PortRequest {

        int port;
        boolean isSuccess;
        String nodeName;

        /**
         * Constructs a new port request for the given node
         * @param nodeName node requesting the port
         */
        public PortRequest(String nodeName) {
            this.nodeName = nodeName;
        }

        /**
         * Accepts a message channel from the requester
         * @param channel channel with the registration request
         */
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

    /**
     * Class to handle request of the list of hosts.
     */
    private class HostListRequest {
        boolean isSuccess;
        List<String> hosts;

        /**
         * Constructs a new request listener.
         */
        public HostListRequest() {
        }

        /**
         * Accepts the host list request on the given channel
         * @param channel channel to receive the request
         */
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

    /**
     * Accepts new messageChannel connections
     * @param onConnection Message channel to accept connections to
     */
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
