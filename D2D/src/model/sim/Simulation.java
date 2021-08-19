package model.sim;

import controller.Logger;
import model.Coordinator;
import model.Peer;
import model.PeerClient;
import model.PeerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simulation of various scenarios to demonstrate functionality
 */
public class Simulation {
    /**
     * Possible scenarios to demonstrate
     */
    public enum Scenario {
        MulticastMove,
        LeaderElection,
        GoHome,
        StopServer
    }

    static Random random = new Random();

    Coordinator coordinator;
    ChaosClient chaosClient;
    List<Peer> peers;
    PeerClient peerClient;

    /**
     * Constructs a new simulation for running the scenarios, with a given coordinator and clients
     * @param coordinator coordinator to use
     * @param chaosClient chaosClient to use
     * @param peerClient PeerClient to use
     */
    public Simulation(Coordinator coordinator, ChaosClient chaosClient, PeerClient peerClient) {
        this.coordinator = coordinator;
        this.chaosClient = chaosClient;
        this.peerClient = peerClient;
        this.peers = new ArrayList<>();
    }

    /**
     * Starts the simulation for the given scenario
     * @param scenario scenario to simulate
     */
    public void start(Scenario scenario) {
        setRegisteredPeers();
        switch(scenario) {
            case MulticastMove:
                movePeers();
                break;
            case LeaderElection:
                electLeader();
                break;
            case GoHome:
                movePeersHome();
                break;
            case StopServer:
                stopServer();
                break;
            default:
                break;
        }

        //this.chaosClient.blockRoute(p.getHostOrIp(), p.getPort(), "bully");
    }

    /**
     * Simulates running a BullyElection
     */
    private void electLeader() {
        Logger.log("Simulating leader election.");
        Peer p = randomPeer();
        this.peerClient.startLeaderElection(p.getHostOrIp(), p.getPort());
        Logger.log(p.toString());
    }

    /**
     * Simulates moving a random peer 4 times
     */
    private void movePeers() {
        Logger.log("Simulating discovering local group");
        int n = 4;
        for(int i = 0; i < n; i++) {
            int x = random.nextInt(1000);
            int y = random.nextInt(1000);
            Peer p = randomPeer();
            Logger.log("Moving " + p + " to (" + x + ", " + y + ")");
            this.peerClient.relocate(p.getHostOrIp(), p.getPort(), x, y);
        }
    }

    /**
     * Simulates moving random peers around, and then tells them all to return to home base by
     * sending the command as a multicast command to a random peer.
     */
    private void movePeersHome() {
//        movePeers();
//        Logger.log("Sleeping for 2 seconds to allow peers to move");
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Peer p = randomPeer();
        Logger.log("Multicasting peers to return to base. Starting at " + p);
        // set home to a random location
        int x = random.nextInt(1000);
        int y = random.nextInt(1000);
        this.peerClient.multicastRelocate(p.getHostOrIp(), p.getPort(), x, y);
    }

    /**
     * Simulates a server going down
     */
    private void stopServer() {
        Logger.log("Simulating a server going down.");
        Peer p = randomPeer();
        this.peerClient.stopServer(p.getHostOrIp(), p.getPort());
        Logger.log(p.toString());
    }


    /**
     * Adds all registered peers from the coordinator to the peers in the simulation.
     */
    private void setRegisteredPeers() {
        for(String name : this.coordinator.getNodes()) {
            String hostOrIp = nodeNameToHostOrIP(name);
            int port = nodeNameToPort(name);
            Peer p = new PeerImpl(hostOrIp, port, null);
            peers.add(p);
        }
    }

    /**
     * Extracts the hostname or ip from a string
     * @param nodeName string of format "host:port"
     * @return the hostname or ip from the string
     */
    private String nodeNameToHostOrIP(String nodeName) {
        return nodeName.split(":")[0];
    }

    /**
     * Extracts the port number from a string.
     * @param nodeName string of format "host:port"
     * @return the port number from the string
     */
    private int nodeNameToPort(String nodeName) {
        return Integer.parseInt(nodeName.split(":")[1]);
    }

    /**
     * Returns a random peer from the list
     * @return a random peer from the list
     */
    private Peer randomPeer() {
        return peers.get(random.nextInt(peers.size()));
    }


}
