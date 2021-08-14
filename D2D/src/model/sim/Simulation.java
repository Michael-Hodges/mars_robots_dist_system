package model.sim;

import model.CoordinatorClient;
import model.Peer;
import model.PeerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {

    static Random random = new Random();
    
    CoordinatorClient coordinator;
    ChaosClient chaosClient;
    List<Peer> peers;



    public Simulation(CoordinatorClient coordinator, ChaosClient chaosClient) {
        this.coordinator = coordinator;
        this.chaosClient = chaosClient;
        this.peers = new ArrayList<>();
    }

    public void start() {
        setRegisteredPeers();




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


}
