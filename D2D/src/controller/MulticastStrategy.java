package controller;

import model.Peer;

public class MulticastStrategy implements RouteStrategy{

  RouteStrategy routeStrategy;
  Peer peer;

  public MulticastStrategy(RouteStrategy routeStrategy, Peer peer) {
    this.routeStrategy = routeStrategy;
    this.peer = peer;
  }

  @Override
  public String getRoute(MessageChannel channel) {
    String route = this.routeStrategy.getRoute(channel);
    if (route.startsWith("multicast")) {
      // handle multicast - check id and flag as seen
      // if bad id, send back garbage route
      multicast(channel);
      return this.routeStrategy.getRoute(channel);
    } else{
      return route;
    }
  }

  private void multicast(MessageChannel channel){

    for (Peer p: this.peer.getPeers()){

    }
  }

}
