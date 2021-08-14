package controller;

import model.sim.ChaosOperation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TCPChaosMessageRouteImpl implements ChaosMessageRouter, RouteStrategy {

    RouteStrategy wrappedRouteStrategy;
    List<String> blockedRoutes;

    public TCPChaosMessageRouteImpl(RouteStrategy routeStrategy) {
        this.wrappedRouteStrategy = routeStrategy;
        this.blockedRoutes = new ArrayList<>();
    }

    public MessageRoute getRoute() {
        return new MessageRoute("chaos", new ChaosListenerFactory());
    }

    @Override
    public String getRoute(MessageChannel channel) {
        String route = wrappedRouteStrategy.getRoute(channel);
        if (this.blockedRoutes.contains(route)) {
            log(route + " message was blocked.");
            return "blocked";
        }
        return route;
    }


    @Override
    public void blockRoute(String route) {
        if (!this.blockedRoutes.contains(route)) {
            this.blockedRoutes.add(route);
            log("Enabling block for " + route + " messages.");
        }
    }

    @Override
    public void unblockRoute(String route) {
        this.blockedRoutes.remove(route);
        log("Disabling block for " + route + " messages.");
    }


    class ChaosListenerFactory implements MessageListenerFactory {

        @Override
        public ActionListener getMessageListener() {
            return new ChaosListener();
        }
    }

    class ChaosListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            MessageEvent event = (MessageEvent)e;
            MessageChannel channel = event.getChannel();
            ChaosOperation operation = ChaosOperation.valueOf(event.getActionCommand());
            switch(operation) {
                case Block:
                    onBlockMessage(channel);
                    break;
                case Unblock:
                    onUnblockMessage(channel);
                    break;
                default:
                    break;
            }
        }

        void onBlockMessage(MessageChannel channel) {
            TCPChaosMessageRouteImpl.this.blockRoute(readRoute(channel));
        }

        void onUnblockMessage(MessageChannel channel) {
            TCPChaosMessageRouteImpl.this.unblockRoute(readRoute(channel));
        }

        String readRoute(MessageChannel channel) {
            String route = null;
            try {
                route = channel.readNextString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return route;
        }

    }

    void log(String message) {
        Logger.log(message);
    }

}
