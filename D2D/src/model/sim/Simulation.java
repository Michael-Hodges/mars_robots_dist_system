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

    public void start() {
        setRegisteredPeers();
        Peer p = randomPeer();
        this.peerClient.startLeaderElection(p.getHostOrIp(), p.getPort());
        Logger.log(p.toString());
        this.chaosClient.blockRoute(p.getHostOrIp(), p.getPort(), "bully");
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
