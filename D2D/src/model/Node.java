package model;


import java.awt.event.ActionListener;

//We will be communicating between nodes via TCP sockets
//become a 'super' node if necessary
public interface Node {
    String getHostAndPort();
    void setListener(ActionListener listener); //will be used to connect to GUI
}
