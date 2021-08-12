package controller;

import model.*;
import view.GuiClient;
import view.GuiServer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Program {

    //This program can run either as the coordinator or as a peer
    public static void main(String[] args) {
        int coordinatorPort = 5000;
        if (args[0].equals("coordinator")) {
            startCoordinator(coordinatorPort);
        } else if(args[0].equals("gui_server")) {
            startGUIView();
        } else if(args[0].equals("bully_election")) {
            int port = Integer.parseInt(args[1]);
            startBullyElection(port);
        }
        else {
            String coordinatorHostOrIP = "localhost";
            startPeerNode(coordinatorHostOrIP, coordinatorPort);
        }
    }

    static void startCoordinator(int port) {
        Coordinator impl = new CoordinatorImpl();
        TCPServer server = new TCPServer(port);
        server.register("coordinator", new CoordinatorProcessDelegateImpl(impl));
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void startPeerNode(String coordinatorHostOrIP, int port) {
        String nodeName = "localhost";
        Coordinator client = new CoordinatorClient(coordinatorHostOrIP, port);
        int clientPort = client.registerNode(nodeName);
        List<Peer> peers = nodesToPeers(client.getNodes());
        System.out.println("Received " + peers.size() + " hosts.");
        GuiClient guiClient = new GuiClient(nodeName + ":" + clientPort);

        PeerImpl impl = new PeerImpl(nodeName, clientPort);
        for(Peer p : peers) {
            impl.add(p);
        }
        impl.setListener(guiClient);
        impl.start();
    }

    static void startGUIView() {
        GuiServer server = new GuiServer();
        server.start();
    }

    static void startBullyElection(int port) {
        System.out.println("Starting bully election");
        String hostOrIp = "localhost";
        Socket conn = null;
        try {
            conn = new Socket(hostOrIp, port);
            SocketChannel channel = new SocketChannel(conn);
            channel.out.writeUTF("peer");
            channel.out.writeUTF("ElectLeader");
            channel.out.writeInt(0);
            channel.out.flush();
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static List<Peer> nodesToPeers(List<String> nodes) {
        List<Peer> peers = new ArrayList<>();
        for(String node : nodes) {
            peers.add(toPeer(node));
        }
        return peers;
    }

    static Peer toPeer(String node) {
        String[] elems = node.split(":");
        return new PeerImpl(elems[0], Integer.parseInt(elems[1]));
    }
}
