package model;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorImpl implements Coordinator {
    List<Address> registeredNodes;

    public CoordinatorImpl() {
        registeredNodes = new ArrayList<>();
    }

    //Note: this method is synchronized to ensure thread safety when assigning ports
    @Override
    public synchronized int registerNode(String nodeName) {
        int port = 5000;
        if (registeredNodes.size() > 0) {
            port = getLastRegistered().port + 1;
        }

        Address a = new Address(nodeName, port);
        registeredNodes.add(a);

        return a.port;
    }

    private Address getLastRegistered() {
        return registeredNodes.get(registeredNodes.size() - 1);
    }

    @Override
    public List<String> getNodes() {
        List<String> nodes = new ArrayList<>();
        for(Address node : registeredNodes) {
            nodes.add(node.toString());
        }
        return nodes;
    }

    private class Address {
        String hostOrIP;
        int port;
        public Address(String hostOrIP, int port) {
            this.hostOrIP = hostOrIP;
            this.port = port;
        }

        @Override
        public String toString() {
            return hostOrIP + ":" + port;
        }
    }
}
