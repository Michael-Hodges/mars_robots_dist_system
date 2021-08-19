package model;

import java.awt.event.ActionEvent;

/**
 * Implementation of ActionEvent for use by peers.
 */
public class ActionPeerEvent extends ActionEvent {

    Object argument;
    PeerEvent eventType;

    /**
     * Constructs new ActionPeerEvent.
     * @param source source of ActionEvent
     * @param id id of ActionEvent
     * @param event PeerEvent to wrap into an ActionPeerEvent
     */
    public ActionPeerEvent(Object source, int id, PeerEvent event) {
        super(source, id, event.toString());
        this.eventType = event;
    }

    /**
     * Returns the argument of this event
     * @return argument of this event
     */
    public Object getArgument() {
        return this.argument;
    }

    /**
     * Sets the argument of this event
     * @param obj object to set as the argument of this event
     */
    public void setArgument(Object obj) {
        this.argument = obj;
    }
}
