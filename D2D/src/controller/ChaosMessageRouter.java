package controller;

public interface ChaosMessageRouter extends MessageRouter {
    void blockRoute(String route);
    void unblockRoute(String route);
}
