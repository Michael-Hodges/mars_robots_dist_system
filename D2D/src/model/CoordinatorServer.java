package model;

import com.sun.security.ntlm.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class CoordinatorServer {

    int port;
    Coordinator coordinator;

    public CoordinatorServer(Coordinator coordinator, int port) {
        this.coordinator = coordinator;
        this.port = port;
    }

    public void run() throws IOException {

        ServerSocket server = new ServerSocket(this.port);
        log("Server initialized on port " + port);

        //TODO: Do we want a timeout?
        while(true) {
            Socket incomingSocket = server.accept();
            log("Client connected.");
            ClientHandler handler = new ClientHandler(incomingSocket);
            Thread t = new Thread(handler);
            t.start();
        }
    }

    void log(String msg) {
        //TODO: Add a timestamp
        Logger.log(msg);
    }

    private class ClientHandler implements Runnable {
        Socket conn;
        public ClientHandler(Socket conn) {
            this.conn = conn;
        }

        @Override
        public void run() {
            try {
                //Code taken from - https://www.baeldung.com/java-inputstream-server-socket
                while(true) {
                    if (conn.isClosed()) {
                        break;
                    }
                    DataInputStream in = new DataInputStream(new BufferedInputStream(this.conn.getInputStream()));
                    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.conn.getOutputStream()));
                    handleInput(in, out);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleInput(DataInputStream in, DataOutputStream out) throws IOException {

            int action = in.readInt();
            if (action == 1) {
                String hostOrIp = in.readUTF();
                CoordinatorServer.this.log("Registering node: " + hostOrIp);
                int port = CoordinatorServer.this.coordinator.registerNode(hostOrIp);
                CoordinatorServer.this.log("Registering port: " + port);
                out.writeInt(1);
                out.writeInt(port);
                out.flush();
                in.close();
                out.close();
                conn.close();
            } else if (action == 2) {
                CoordinatorServer.this.log("Retrieving nodes...");
                out.writeInt(2);
                List<String> nodes = CoordinatorServer.this.coordinator.getNodes();
                out.writeInt(nodes.size());
                for(String node : nodes) {
                    out.writeUTF(node);
                }
                out.flush();
                in.close();
                out.close();
                conn.close();
            } else {
                CoordinatorServer.this.log("Error reading client. Unknown command received.");
                out.write(-1); //signal an error
                out.writeUTF("Unrecognized command received.");
            }
        }
    }
}
