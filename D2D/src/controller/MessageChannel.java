package controller;

import java.io.*;

/**
 * The message channel interface, which PeersUse to communicate with each other.
 */
public interface MessageChannel {
    /**
     * Write a string into the channel
     * @param s string to write into the channel
     * @throws IOException Java socket/IO exceptions
     */
    void writeString(String s) throws IOException;

    /**
     * Write an int into the channel
     * @param n int to write into the channel
     * @throws IOException Java socket/IO exceptions
     */
    void writeInt(int n) throws IOException;

    /**
     * Read a string out of the channel, which consumes it and removes it from the channel
     * @return String from the channel
     * @throws IOException Java socket/io exceptions
     */
    String readNextString() throws IOException;

    /**
     * Read an int out of the channel, which consumes it and removes it from the channel
     * @return Int from the channel
     * @throws IOException Java socket/io exception
     */
    int readNextInt() throws IOException;

    /**
     * Closes the channel, flushing the output first.
     */
    void close();

    /**
     * Flushes the output of the channel.
     */
    void flush();
}
