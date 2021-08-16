package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of a Coordinator.
 */
public class CoordinatorImpl implements Coordinator {
    List<Address> registeredNodes;

    /**
     * Constructor for the coordinator implementation
     */
    public CoordinatorImpl() {
        registeredNodes = new ArrayList<>();
    }

    //Note: this method is synchronized to ensure thread safety when assigning ports
    @Override
    public synchronized int registerNode(String nodeName) {
        int port = 5001;
        if (registeredNodes.size() > 0) {
            port = getLastRegistered().port + 1;
        }

        Address a = new Address(nodeName, port);
        registeredNodes.add(a);

        return a.port;
    }

    /**
     * Returns the most recently registered node
     * @return the most recently registered node
     */
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

    /**
     * A representation of an address with a host and port
     */
    private class Address {
        String hostOrIP;
        int port;

        /**
         * Creates a new address object
         * @param hostOrIP host/ip of the address
         * @param port port of the address
         */
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
