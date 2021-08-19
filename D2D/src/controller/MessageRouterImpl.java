package controller;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of MessageRouter interface, can register individual routes, and return them on
 * command.
 */
public class MessageRouterImpl implements MessageRouter {
    List<MessageRoute> messageRoutes;
    RouteStrategy routeStrategy;

    /**
     * Constructs new router
     */
    public MessageRouterImpl() {
        this(new RouteStrategyImpl());
    }

    /**
     * Constructs new MessageRouter, with the given routeStrategy
     * @param routeStrategy routeStrategy to use with this messageRouter
     */
    public MessageRouterImpl(RouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
        this.messageRoutes = new ArrayList<>();
    }

    @Override
    public void registerRoute(MessageRoute messageRoute) {
        if (!this.messageRoutes.contains(messageRoute)) {
            this.messageRoutes.add(messageRoute);
        }
    }

    @Override
    public void route(int requestId, MessageChannel channel) {
        String route = getRoute(channel);
        String message = getMessage(channel);
        for (MessageRoute p : this.messageRoutes) {
            if (p.identifier.equals(route)) {
                MessageEvent messageEvent = new MessageEvent(this, requestId, channel, message);
                ActionListener messageListener = p.messageListenerFactory.getMessageListener();
                messageListener.actionPerformed(messageEvent);
            }
        }
    }

    @Override
    public String getRoute(MessageChannel channel) {
        return routeStrategy.getRoute(channel);
    }

    /**
     * Gets the message associated with a channel
     * @param channel channel to get message from
     * @return the message from the channel
     */
    private String getMessage(MessageChannel channel) {
        try {
            return channel.readNextString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
