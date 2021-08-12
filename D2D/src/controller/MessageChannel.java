package controller;

import java.io.*;

public interface MessageChannel {
    void writeString(String s) throws IOException;
    void writeInt(int n) throws IOException;
    String readNextString() throws IOException;
    int readNextInt() throws IOException;
    void close();
    void flush();
}
