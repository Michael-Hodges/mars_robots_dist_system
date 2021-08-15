package model.sim;

import controller.Logger;
import controller.MessageChannelFactory;
import model.Coordinator;
import model.Peer;
import model.PeerClient;
import model.PeerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {

    public enum Scenario {
        Multicast,
        LeaderElection,
        GoHome
    }

    static Random random = new Random();

    Coordinator coordinator;
    ChaosClient chaosClient;
    List<Peer> peers;
    PeerClient peerClient;


    public Simulation(Coordinator coordinator, ChaosClient chaosClient, PeerClient peerClient) {
        this.coordinator = coordinator;
        this.chaosClient = chaosClient;
        this.peerClient = peerClient;
        this.peers = new ArrayList<>();
    }

    public void start(Scenario scenario) {
        setRegisteredPeers();
        switch(scenario) {
            case Multicast:
                movePeers();
                break;
            case LeaderElection:
                electLeader();
                break;
            case GoHome:
                movePeersHome();
                break;
            default:
                break;
        }

        //this.chaosClient.blockRoute(p.getHostOrIp(), p.getPort(), "bully");
    }

    private void electLeader() {
        Logger.log("Simulating leader election.");
        Peer p = randomPeer();
        this.peerClient.startLeaderElection(p.getHostOrIp(), p.getPort());
        Logger.log(p.toString());
    }

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

    private void movePeersHome() {
        movePeers();
        Logger.log("Sleeping for 2 seconds to allow peers to move");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.log("Multicasting peers to return to base");
        Peer p = randomPeer();
        // set home to a random location
        int x = random.nextInt(1000);
        int y = random.nextInt(1000);
        this.peerClient.multicastRelocate(p.getHostOrIp(), p.getPort(), x, y);
    }


    private void setRegisteredPeers() {
        for(String name : this.coordinator.getNodes()) {
            String hostOrIp = nodeNameToHostOrIP(name);
            int port = nodeNameToPort(name);
            Peer p = new PeerImpl(hostOrIp, port, null);
            peers.add(p);
        }
    }

    private String nodeNameToHostOrIP(String nodeName) {
        return nodeName.split(":")[0];
    }

    private int nodeNameToPort(String nodeName) {
        return Integer.parseInt(nodeName.split(":")[1]);
    }

    private Peer randomPeer() {
        return peers.get(random.nextInt(peers.size()));
    }


}
