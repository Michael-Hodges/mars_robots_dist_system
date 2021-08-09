package model;

import java.io.*;
import java.net.Socket;
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

        public void accept(SocketChannel channel) {
            this.port = -1;
            this.isSuccess = false;
            try {
                log("Registering: " + nodeName);
                channel.out.writeInt(1);
                channel.out.writeUTF(nodeName);
                channel.out.flush();
                int successCode = channel.in.readInt();
                if (successCode == 1) {
                    this.port = channel.in.readInt();
                    this.isSuccess = true;
                }
                channel.in.close();
                channel.out.close();
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

        public void accept(SocketChannel channel) {
            this.isSuccess = false;
            this.hosts = new ArrayList<>();
            try {
                log("Retrieving hosts...");
                channel.out.writeInt(2);
                channel.out.flush();
                int successCode = channel.in.readInt();
                if (successCode == 2) {
                    int nodeCount = channel.in.readInt();
                    while(nodeCount > 0) {
                        String host = channel.in.readUTF();
                        log("Received - " + host);
                        hosts.add(host);
                        nodeCount--;
                    }
                    this.isSuccess = true;
                }
                channel.in.close();
                channel.out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendReceive(Consumer<SocketChannel> onConnection) {
        try {
            Socket conn = new Socket(this.hostOrIp, this.port);

            DataInputStream in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
            SocketChannel channel = new SocketChannel(in, out);
            onConnection.accept(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void log(String msg) {
        Logger.log(msg);
    }
}
