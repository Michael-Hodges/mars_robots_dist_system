package controller;

/**
 * Wrapper interface for getting the Route of a channel out of a MessageRoute.
 */
public interface RouteStrategy {
    /**
     * Returns the route of the given channel
     * @param channel channel to get route from
     * @return the name of the route
     */
    String getRoute(MessageChannel channel);
}
