package controller;

import java.io.IOException;

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
