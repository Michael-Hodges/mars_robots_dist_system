package controller;

/**
 * Message Router to introduce chaos: i.e. it can block and unblock channels to simulate a
 * connection loss.
 */
public interface ChaosMessageRouter {
    /**
     * Blocks a route to simulate connection loss
     * @param route route to block
     */
    void blockRoute(String route);

    /**
     * Unblocks route, to restore connection
     * @param route route to unblock
     */
    void unblockRoute(String route);
}
