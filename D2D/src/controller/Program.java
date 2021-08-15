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

/**
 * Driver class to spin up a "NASA" Coordinator, some peers, start a bully election, start the
 * simulation, or start the GUI viewer
 */
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

    /**
     * Starts the NASA Coordinator node.
     * @param port the port for the coordinator to listen on
     */
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

    /**
     * Starts a Peer Node, registers it with the coordinator, and registers it with all already
     * existing Peers
     * @param coordinatorHostOrIP hostname/ip of coordinator to contact
     * @param port port of coordinator to contact
     */
    static void startPeerNode(String coordinatorHostOrIP, int port) {
        String nodeName = "localhost";
        Coordinator client = new CoordinatorClient(coordinatorHostOrIP, port);
        int clientPort = client.registerNode(nodeName);
        List<Peer> peers = nodesToPeers(client.getNodes());
        System.out.println("Received " + peers.size() + " hosts.");

        MessageChannelFactory messageChannelFactory = new MessageChannelFactoryImpl();
        PeerImpl impl = new PeerImpl(nodeName, clientPort, messageChannelFactory);
        PeerEventHandler peerEventHandler = new PeerEventHandler(impl);

        for(Peer p : peers) {
            impl.add(p);
        }
        impl.setListener(peerEventHandler);
        impl.start();
    }

    /**
     * Launches the GUI Viewer
     */
    static void startGUIView() {
        DashboardServer server = new DashboardServer();
        server.start();
    }

    /**
     * Contacts the Peer at the given port in order to start the bully election
     * @param port port of peer to contact
     */
    static void startBullyElection(int port) {
        System.out.println("Starting bully election");
        String hostOrIp = "localhost";
        PeerClient client = new PeerClient(new MessageChannelFactoryImpl());
        client.startLeaderElection(hostOrIp, port);
    }

    /**
     * Starts the simulation with a coordinator at the given host/ip and port, using a chaos
     * client and peer client.
     * @param coordinatorHostOrIP host/ip to use for the coordinator
     * @param port port to use for the coordinator
     */
    static void startSimulation(String coordinatorHostOrIP, int port) {
        MessageChannelFactory messageChannelFactory = new MessageChannelFactoryImpl();
        Coordinator coordinator = new CoordinatorClient(coordinatorHostOrIP, port);
        ChaosClient chaosClient = new ChaosClient(messageChannelFactory);
        PeerClient peerClient = new PeerClient(messageChannelFactory);
        Simulation simulation = new Simulation(coordinator, chaosClient, peerClient);
        simulation.start();
    }

    /**
     * Takes a list of Strings representing nodes, and turns them into PeerImpl objects
     * @param nodes list of strings
     * @return List of Peers
     */
    static List<Peer> nodesToPeers(List<String> nodes) {
        List<Peer> peers = new ArrayList<>();
        for(String node : nodes) {
            peers.add(toPeer(node));
        }
        return peers;
    }

    /**
     * Deconstructs a string of the format "host:port" in order to create a new PeerImpl object
     * @param node string of format "host:port", where host is the hostname or ip of the node and
     *            port is the port of the node
     * @return a PeerImpl object representative of the node
     */
    static Peer toPeer(String node) {
        String[] elems = node.split(":");
        return new PeerImpl(elems[0], Integer.parseInt(elems[1]), new MessageChannelFactoryImpl());
    }
}
