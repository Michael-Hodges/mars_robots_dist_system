package model;

public interface Robot {
    /*
    1. Call Coordinator and register self
    2. Call Coordinator to get list of all nodes
    3. Use leader algorithm to determine whether you are leader or participant against list of nodes
    4. Fall in line
     */

    int getID(); //must be a globally unique identifier - necessary for consensus and leader election
}
