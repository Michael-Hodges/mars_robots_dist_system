package model;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPMessageChannelImpl implements MessageChannel {
    private Socket conn;
    private DataInputStream in;
    private DataOutputStream out;

    public TCPMessageChannelImpl(String hostOrIp, int port) throws IOException {
        this.initialize(new Socket(hostOrIp, port));
    }

    public TCPMessageChannelImpl(Socket socket) {
        this.initialize(socket);
    }

    private void initialize(Socket conn) {
        this.conn = conn;
        try {
            this.in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
        } catch (IOException e) {
            Logger.log(e.getMessage());
        }
    }


    @Override
    public void writeString(String s) throws IOException {
        this.out.writeUTF(s);
    }

    @Override
    public void writeInt(int n) throws IOException {
        this.out.writeInt(n);
    }

    @Override
    public String readNextString() throws IOException {
        return this.in.readUTF();
    }

    @Override
    public int readNextInt() throws IOException {
        return this.in.readInt();
    }

    public void close() {
        try {
            this.out.flush();
            this.out.close();
            this.in.close();
            this.conn.close();
        } catch (IOException e) {
            Logger.log(e.getMessage());
        }
    }

    @Override
    public void flush() {
        try {
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
