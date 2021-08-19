package controller.tcp;

import controller.*;
import controller.MessageRoute;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server used by peers to listen to messages from other peers.
 */
public class TCPServer {

    int requestCounter = 0;
    int port;
    MessageRouter messageRouter;


    /**
     * Construct new server, with a given port, and create a new message router.
     * @param port port to listen on
     */
    public TCPServer(int port) {
        this.port = port;
        this.messageRouter = new MessageRouterImpl();
    }

    /**
     * Construct new server with a given port and given messageRouter
     * @param port port to listen on
     * @param messageRouter messageRouter to use
     */
    public TCPServer(int port, MessageRouter messageRouter) {
        this.port = port;
        this.messageRouter = messageRouter;
    }

    /**
     * Runs server, when a socket connection is received, a new message channel is created, and a
     * new thread is spawned for the message router to route the connection in the channel
     * @throws IOException Java exceptions with connecting to sockets
     */
    public void run() throws IOException {

        ServerSocket server = new ServerSocket(this.port);
        log("Server initialized on port " + port);

        while (true) {
            this.requestCounter++;
            Socket incomingSocket = server.accept();
            incomingSocket.setSoTimeout(3000);
            MessageChannel channel = new TCPMessageChannelImpl(incomingSocket);
            log("Client connected.");
            Thread t = new Thread(() -> messageRouter.route(this.requestCounter, channel));
            t.start();
        }
    }

    /**
     * Registers new routes in the message router, using a process name (such as peer or bully),
     * and a message listener factory
     * @param processName the process name to associate with the new MessageRoute
     * @param messageListenerFactory the MessageListenerFactory to provide to the route
     */
    public void register(String processName, MessageListenerFactory messageListenerFactory){
        this.messageRouter.registerRoute(new MessageRoute(processName, messageListenerFactory));
    }

    void log(String msg) {
        System.out.println(msg);
    }

}
