package controller;

public interface MessageRouter {
    void registerRoute(MessageRoute messageRoute);
    void route(int requestId, MessageChannel channel);
    String getRoute(MessageChannel channel);
}
