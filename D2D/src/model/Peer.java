package model;


import java.awt.event.ActionListener;

//We will be communicating between nodes via TCP sockets
//become a 'super' node if necessary
public interface Peer {
    String getHostAndPort();
    String getHostOrIp();
    int getPort();
    void setListener(ActionListener listener); //will be used to connect to GUI
    void sendRegisterRequestTo(Peer peer);
    void add(Peer peer);
}
