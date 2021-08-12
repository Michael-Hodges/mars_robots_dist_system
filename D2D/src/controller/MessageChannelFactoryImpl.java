package controller;

import controller.tcp.TCPMessageChannelImpl;

import java.io.IOException;

public class MessageChannelFactoryImpl implements MessageChannelFactory {

    public MessageChannel getChannel(String hostOrIp, int port) throws IOException {
        return new TCPMessageChannelImpl(hostOrIp, port);
    }

}
