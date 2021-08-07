package model;

import java.awt.event.ActionListener;
import java.net.Socket;

public class NodeImpl implements Node{
    ActionListener listener;
    String host;
    int port;
    Thread serverThread;

    @Override
    public String getHostAndPort() {
        return host + ":" + port;
    }

    @Override
    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    private void startServer() {
        //implement it here - send to new interruptable thread
        //set this.serverThread to new thread
    }

    private void stopServer() {
        //interrupt and stop this.serverThread
    }

    // get and return the TCP socket
    private Socket connect(String hostOrIP, int port) {
        return null;
    }
}
