package controller;

import java.net.ServerSocket;

/**
 * Routes messages on given routes.
 */
public interface MessageRouter {

    /**
     * Add a new route to this router
     * @param messageRoute route to add to this router
     */
    void registerRoute(MessageRoute messageRoute);

    /**
     * Chooses a route out of all stored routes to send the message channel across, and send an
     * event to the message listener associated with that route
     * @param requestId id of the request
     * @param channel channel carrying request
     */
    void route(int requestId, MessageChannel channel);

    /**
     * Gets the route associated with a channel
     * @param channel channel to get route from
     * @return the name of the route
     */
    String getRoute(MessageChannel channel);
}
