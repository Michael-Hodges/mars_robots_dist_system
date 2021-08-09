package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SocketChannel {
    DataInputStream in;
    DataOutputStream out;

    public SocketChannel(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }
}
