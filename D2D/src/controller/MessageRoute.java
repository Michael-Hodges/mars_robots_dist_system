package controller;

/**
 * Route for holding message channels
 */
public class MessageRoute {

    String identifier;
    MessageListenerFactory messageListenerFactory;

    /**
     * Constructs a message route with the given identifier and message listener factory
     * @param identifier identifier of what sort of route it is (i.e peer or bully)
     * @param messageListenerFactory messageListenerFactory to use for the route to generate
     *                               message listeners in the message router
     */
    public MessageRoute(String identifier, MessageListenerFactory messageListenerFactory) {
        this.identifier = identifier;
        this.messageListenerFactory = messageListenerFactory;
    }
}
