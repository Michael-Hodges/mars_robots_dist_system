package controller;

import java.io.IOException;

/**
 * Concrete implementation of RouteStrategy interface.
 */
public class RouteStrategyImpl implements RouteStrategy{
    @Override
    public String getRoute(MessageChannel channel) {
        try {
            return channel.readNextString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
