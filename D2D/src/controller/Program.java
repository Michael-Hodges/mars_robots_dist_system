package controller;

import controller.tcp.TCPMessageChannelImpl;
import controller.tcp.TCPServer;
import model.*;
import model.sim.ChaosClient;
import model.sim.Simulation;
import view.PeerEventHandler;
import view.DashboardServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Program {

    //This program can run either as the coordinator or as a peer
    public static void main(String[] args) {
        int coordinatorPort = 5000;
        String coordinatorHostOrIP = "localhost";
        if (args[0].equals("coordinator")) {
            startCoordinator(coordinatorPort);
        } else if(args[0].equals("gui_server")) {
            startGUIView();
        } else if(args[0].equals("bully_election")) {
            int port = Integer.parseInt(args[1]);
            startBullyElection(port);
        } else if (args[0].equals("simulation")) {
            startSimulation(coordinatorHostOrIP, coordinatorPort);
        }
        else {
            startPeerNode(coordinatorHostOrIP, coordinatorPort);
        }
    }

    static void startCoordinator(int port) {
        Coordinator impl = new CoordinatorImpl();
        TCPServer server = new TCPServer(port);
        server.register("coordinator", new CoordinatorMessageListenerFactoryImpl(impl));
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

        MessageChannelFactory messageChannelFactory = new MessageChannelFactoryImpl();
        PeerImpl impl = new PeerImpl(nodeName, clientPort, messageChannelFactory);
        PeerEventHandler peerEventHandler = new PeerEventHandler(impl);
        impl.setListener(peerEventHandler);
        for(Peer p : peers) {
            impl.add(p);
        }
        impl.start();
    }

    static void startGUIView() {
        DashboardServer server = new DashboardServer();
        server.start();
    }

    static void startBullyElection(int port) {
        System.out.println("Starting bully election");
        String hostOrIp = "localhost";
        PeerClient client = new PeerClient(new MessageChannelFactoryImpl());
        client.startLeaderElection(hostOrIp, port);
    }

    static void startSimulation(String coordinatorHostOrIP, int port) {
        MessageChannelFactory messageChannelFactory = new MessageChannelFactoryImpl();
        Coordinator coordinator = new CoordinatorClient(coordinatorHostOrIP, port);
        ChaosClient chaosClient = new ChaosClient(messageChannelFactory);
        PeerClient peerClient = new PeerClient(messageChannelFactory);
        Simulation simulation = new Simulation(coordinator, chaosClient, peerClient);
        simulation.start();
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
        return new PeerImpl(elems[0], Integer.parseInt(elems[1]), new MessageChannelFactoryImpl());
    }
}
