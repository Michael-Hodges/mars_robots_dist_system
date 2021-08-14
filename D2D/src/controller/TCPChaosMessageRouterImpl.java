package controller;

import model.sim.ChaosOperation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

public class TCPChaosMessageRouterImpl implements ChaosMessageRouter {

    MessageRouter wrappedRouter;
    List<String> blockedRoutes;

    public TCPChaosMessageRouterImpl(MessageRouter wrappedRouter) {
        this.wrappedRouter = wrappedRouter;
        this.blockedRoutes = new ArrayList<>();
        MessageRoute route = new MessageRoute("chaos", new ChaosListenerFactory());
        this.registerRoute(route);
    }

    @Override
    public void registerRoute(MessageRoute messageRoute) {
        this.wrappedRouter.registerRoute(messageRoute);
    }

    @Override
    public void route(int requestId, MessageChannel channel) {
        this.wrappedRouter.route(requestId, channel);
    }

    @Override
    public String getRoute(MessageChannel channel) {
        String route = this.wrappedRouter.getRoute(channel);
        if (this.blockedRoutes.contains(route)) {
            return "blocked";
        }
        return route;
    }


    @Override
    public void blockRoute(String route) {
        if (!this.blockedRoutes.contains(route)) {
            this.blockedRoutes.add(route);
        }
    }

    @Override
    public void unblockRoute(String route) {
        this.blockedRoutes.remove(route);
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
            TCPChaosMessageRouterImpl.this.blockRoute(readRoute(channel));
        }

        void onUnblockMessage(MessageChannel channel) {
            TCPChaosMessageRouterImpl.this.unblockRoute(readRoute(channel));
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



}
