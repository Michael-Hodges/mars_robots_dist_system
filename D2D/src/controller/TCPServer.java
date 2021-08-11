package controller;

import model.ProcessDelegate;
import model.SocketChannel;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {

    int requestCounter = 0;
    int port;
    List<Process> registered;
    RequestHandler requestHandler;


    public TCPServer(int port) {
        this.port = port;
        this.registered = new ArrayList<>();
        this.requestHandler = new RequestHandler();
    }

    void run() throws IOException {

        ServerSocket server = new ServerSocket(this.port);
        log("Server initialized on port " + port);

        //TODO: Do we want a timeout?
        while (true) {
            this.requestCounter++;
            Socket incomingSocket = server.accept();
            log("Client connected.");
            Thread t = new Thread(() -> requestHandler.delegate(this.requestCounter, incomingSocket));
            t.start();
        }
    }

    void register(String processName, ProcessDelegate processDelegate){
        this.registered.add(new Process(processName, processDelegate));
    }

    void log(String msg) {
        System.out.println(msg);
    }

    class Process {
        String identifier;
        ProcessDelegate delegate;
        public Process(String identifier, ProcessDelegate delegate) {
            this.identifier = identifier;
            this.delegate = delegate;
        }
    }


    class RequestHandler {

        public void delegate(int requestId, Socket conn) {
            SocketChannel channel = new SocketChannel(conn);
            String targetProcess = readUTF(channel);
            String targetMessage = readUTF(channel);
            for (Process p : TCPServer.this.registered) {
                if (p.identifier.equals(targetProcess)) {
                    TCPMessageEvent event = new TCPMessageEvent(this, requestId, channel, targetMessage);
                    ActionListener listener = p.delegate.onConnection();
                    listener.actionPerformed(event);
                }
            }
        }

        private String readUTF(SocketChannel channel) {
            try {
                return channel.in.readUTF();
            } catch (IOException e) {
                log(e.getMessage());
            }
            return null;
        }
    }
}
