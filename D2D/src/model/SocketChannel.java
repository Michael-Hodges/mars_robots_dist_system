package model;

import java.io.*;
import java.net.Socket;

public class SocketChannel {
    private Socket conn;
    public DataInputStream in;
    public DataOutputStream out;

    public SocketChannel(Socket conn) {
        this.conn = conn;
        try {
            this.in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.out.close();
            this.in.close();
            this.conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
