package controller;


import java.io.IOException;

/**
 * Interface to get message channels given a host/ip and port number
 */
public interface MessageChannelFactory {
    /**
     * Returns new message channel with given parameters
     * @param hostOrIp host/ip to connect to with channel
     * @param port port to connect to host on
     * @return new message channel
     * @throws IOException Java socket/io errors
     */
    MessageChannel getChannel(String hostOrIp, int port) throws IOException;
}
