package model;

import java.io.*;
import java.net.Socket;

public interface MessageChannel {
    void writeString(String s) throws IOException;
    void writeInt(int n) throws IOException;
    String readNextString() throws IOException;
    int readNextInt() throws IOException;
    void close();
    void flush();
}
