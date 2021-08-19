package model;

import java.util.List;

// Serves as initial registration at start-up to tell everyone who
// is in the system. And then goes quiet.

/**
 * Interface for Coordinator that all peers register themselves with
 */
public interface Coordinator {
    /**
     * Registers a node with the coordinator
     * @param nodeName string used to represent a node to be registered
     * @return returns the port number to be used by the node
     */
    int registerNode(String nodeName); //send ID and returns your port - start above 5000

    /**
     * Returns all nodes that have been registered with the coordinator
     * @return List of strings representing nodes
     */
    List<String> getNodes(); //use order in this list to create ID
}
