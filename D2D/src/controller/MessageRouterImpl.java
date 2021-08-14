package controller;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageRouterImpl implements MessageRouter {
    List<MessageRoute> messageRoutes;

    public MessageRouterImpl() {
        this.messageRoutes = new ArrayList<>();
    }

    public void registerRoute(MessageRoute messageRoute) {
        if (!this.messageRoutes.contains(messageRoute)) {
            this.messageRoutes.add(messageRoute);
        }
    }

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

    public String getRoute(MessageChannel channel) {
        try {
            return channel.readNextString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMessage(MessageChannel channel) {
        try {
            return channel.readNextString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
