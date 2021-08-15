package controller;

public interface ChaosMessageRouter {
    void blockRoute(String route);
    void unblockRoute(String route);
}
