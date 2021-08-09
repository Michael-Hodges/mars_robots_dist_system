package controller;

import model.Coordinator;
import model.CoordinatorClient;
import model.CoordinatorImpl;
import model.CoordinatorServer;

import java.io.IOException;
import java.util.List;

public class Program {

    //This program can run either as the coordinator or as a peer
    public static void main(String[] args) {
        int coordinatorPort = 5000;
        if (args[0].equals("coordinator")) {
            startCoordinator(coordinatorPort);
        } else {
            String coordinatorHostOrIP = "localhost";
            startPeerNode(coordinatorHostOrIP, coordinatorPort);
        }
    }

    static void startCoordinator(int port) {
        Coordinator impl = new CoordinatorImpl();
        CoordinatorServer server = new CoordinatorServer(impl, port);
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void startPeerNode(String coordinatorHostOrIP, int port) {
        Coordinator client = new CoordinatorClient(coordinatorHostOrIP, port);
        int clientPort = client.registerNode("localhost");
        List<String> hosts = client.getNodes();
        System.out.println("Received " + hosts.size() + " hosts.");
    }
}
