package controller;

import model.sim.ChaosOperation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of ChaosMessageRouter, and the RouteStrategy
 */
public class TCPChaosMessageRouteImpl implements ChaosMessageRouter, RouteStrategy {

    RouteStrategy wrappedRouteStrategy;
    List<String> blockedRoutes;

    /**
     * Constructs new TCPChaosMessage Route
     * @param routeStrategy the routeStrategy to use
     */
    public TCPChaosMessageRouteImpl(RouteStrategy routeStrategy) {
        this.wrappedRouteStrategy = routeStrategy;
        this.blockedRoutes = new ArrayList<>();
    }

    /**
     * Returns the MessageRoute, a new MessageRoute with identifier of chaos, with a new
     * ChaosListener Factory
     * @return a new MessageRoute
     */
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

    /**
     * Creates new ChaosListener objects
     */
    class ChaosListenerFactory implements MessageListenerFactory {

        @Override
        public ActionListener getMessageListener() {
            return new ChaosListener();
        }
    }

    /**
     * ActionListener for ChaosOperation events.
     */
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

        /**
         * Blocks the route specified in the channel
         * @param channel channel carrying communication
         */
        void onBlockMessage(MessageChannel channel) {
            TCPChaosMessageRouteImpl.this.blockRoute(readRoute(channel));
        }

        /**
         * Unblocks the route specified in the channel
         * @param channel channel carrying communication
         */
        void onUnblockMessage(MessageChannel channel) {
            TCPChaosMessageRouteImpl.this.unblockRoute(readRoute(channel));
        }

        /**
         * Reads the route out of the channel
         * @param channel channel to read route of
         * @return the route from the channel
         */
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

    /**
     * Logs given string to console
     * @param message message to log to console
     */
    void log(String message) {
        Logger.log(message);
    }

}
