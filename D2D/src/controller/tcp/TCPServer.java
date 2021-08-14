package controller.tcp;

import controller.*;
import controller.MessageRoute;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    int requestCounter = 0;
    int port;
    MessageRouterImpl messageRouter;


    public TCPServer(int port) {
        this.port = port;
        this.messageRouter = new MessageRouterImpl();
    }

    public void run() throws IOException {

        ServerSocket server = new ServerSocket(this.port);
        log("Server initialized on port " + port);

        //TODO: Do we want a timeout?
        while (true) {
            this.requestCounter++;
            Socket incomingSocket = server.accept();
            MessageChannel channel = new TCPMessageChannelImpl(incomingSocket);
            log("Client connected.");
            Thread t = new Thread(() -> messageRouter.route(this.requestCounter, channel));
            t.start();
        }
    }

    public void register(String processName, MessageListenerFactory messageListenerFactory){
        this.messageRouter.registerRoute(new MessageRoute(processName, messageListenerFactory));
    }

    void log(String msg) {
        System.out.println(msg);
    }

}
