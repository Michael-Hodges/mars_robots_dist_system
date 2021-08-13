package model;

import java.util.List;

// Serves as initial registration at start-up to tell everyone who
// is in the system. And then goes quiet.
public interface Coordinator {
    int registerNode(String nodeName); //send ID and returns your port - start above 5000
    List<String> getNodes(); //use order in this list to create ID
}
