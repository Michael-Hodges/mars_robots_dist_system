package controller;

public class MessageRoute {
    String identifier;
    MessageListenerFactory messageListenerFactory;
    public MessageRoute(String identifier, MessageListenerFactory messageListenerFactory) {
        this.identifier = identifier;
        this.messageListenerFactory = messageListenerFactory;
    }
}
