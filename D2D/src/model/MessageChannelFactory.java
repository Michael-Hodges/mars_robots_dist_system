package model;

import java.io.IOException;

public interface MessageChannelFactory {

    MessageChannel getChannel(String hostOrIp, int port) throws IOException;
}
