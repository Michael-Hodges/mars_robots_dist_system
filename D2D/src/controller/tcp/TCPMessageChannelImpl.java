package controller.tcp;

import model.Logger;
import controller.MessageChannel;

import java.io.*;
import java.net.Socket;

/**
 * TCP implementation of the MessageChannel interface, provides communication channels between
 * nodes.
 */
public class TCPMessageChannelImpl implements MessageChannel {
    private Socket conn;
    private DataInputStream in;
    private DataOutputStream out;

    /**
     * Creates new message channel, using a socket created from the supplied host and port
     * @param hostOrIp ip or hostname to use
     * @param port port to use
     * @throws IOException Java exception thrown when Socket is created
     */
    public TCPMessageChannelImpl(String hostOrIp, int port) throws IOException {
        this.initialize(new Socket(hostOrIp, port));
    }

    /**
     * Creates new MessageChannel with given socket.
     * @param socket socket to use to create channel
     */
    public TCPMessageChannelImpl(Socket socket) {
        this.initialize(socket);
    }

    /**
     * Sets this objects in and out streams using the given socket.
     * @param conn Socket to use for input and output streams
     */
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

    @Override
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
