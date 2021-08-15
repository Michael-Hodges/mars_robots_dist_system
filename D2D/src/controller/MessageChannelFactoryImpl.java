package controller;

import controller.tcp.TCPMessageChannelImpl;

import java.io.IOException;

/**
 * Concrete implementation of MessageChannelFactory, produces TCPMessageChannels
 */
public class MessageChannelFactoryImpl implements MessageChannelFactory {

    @Override
    public MessageChannel getChannel(String hostOrIp, int port) throws IOException {
        return new TCPMessageChannelImpl(hostOrIp, port);
    }

}
