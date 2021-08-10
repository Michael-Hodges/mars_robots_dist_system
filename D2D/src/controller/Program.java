package controller;

import model.*;
import view.GuiClient;
import view.GuiServer;

import java.io.IOException;
import java.util.List;

public class Program {

    //This program can run either as the coordinator or as a peer
    public static void main(String[] args) {
        int coordinatorPort = 5000;
        if (args[0].equals("coordinator")) {
            startCoordinator(coordinatorPort);
        } else if(args[0].equals("gui_server")) {
            startGUIView();
        }
        else {
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
        String nodeName = "10.0.0.1";
        Coordinator client = new CoordinatorClient(coordinatorHostOrIP, port);
        int clientPort = client.registerNode(nodeName);
        GuiClient guiClient = new GuiClient(nodeName + ":" + clientPort);
        NodeImpl impl = new NodeImpl(nodeName, clientPort);
        impl.setListener(guiClient);

        impl.start();

        List<String> hosts = client.getNodes();
        System.out.println("Received " + hosts.size() + " hosts.");
    }

    static void startGUIView() {
        GuiServer server = new GuiServer();
        server.start();
    }
}
