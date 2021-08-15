package model;


import java.awt.event.ActionListener;
import java.util.List;


/**
 * Peer interface, for communication between peers in mars.
 */
public interface Peer {
    /**
     * Returns the hostname/ip and port of a peer
     * @return the hostname or ip and port
     */
    String getHostAndPort();

    /**
     * Returns only the hostname/ip of a peer
     * @return the hostnae or ip of a peer
     */
    String getHostOrIp();

    /**
     * Returns the port of a peer
     * @return the port of this peer
     */
    int getPort();

    /**
     * Adds an actionlistener to the peer, to communicate with a Gui
     * @param listener the listener to add
     */
    void setListener(ActionListener listener); //will be used to connect to GUI

    /**
     * Sends request to another peer in order to register this peer with its neighbors.
     * @param peer peer to send request to
     */
    void sendRegisterRequestTo(Peer peer);

    /**
     * Action to electLeader in bully algorithm
     */
    void electLeader();

    /**
     * Returns the peer that this peer recognizes as the leader
     * @return the peer that is the leader
     */
    Peer getLeader();

    /**
     * Adds a given peer to this peer's list of known neighbors
     * @param peer peer to ad to list
     */
    void add(Peer peer);

    /**
     * Returns the list of neighbors that this peer recognizes
     * @return list of this peers neighbors
     */
    List<Peer> getPeers();
}
