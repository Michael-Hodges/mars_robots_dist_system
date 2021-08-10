package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class NodeImpl implements Node{
    ActionListener listener;
    String host;
    int port;
    Thread serverThread;
    public NodeImpl(String hostOrIP, int port) {
        this.host = hostOrIP;
        this.port = port;
    }

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
        ActionEvent ev = new ActionEvent(this, 1, "startServer");
        listener.actionPerformed(ev);
    }

    private void stopServer() {
        //interrupt and stop this.serverThread
        ActionEvent ev = new ActionEvent(this, 1, "stopServer");
        listener.actionPerformed(ev);
    }

    // get and return the TCP socket
    private Socket connect(String hostOrIP, int port) {
        return null;
    }

    public void start() {
        startServer();
        try {
            Thread.sleep(3000); //just to demo color changing in GUI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopServer();
    }
}
