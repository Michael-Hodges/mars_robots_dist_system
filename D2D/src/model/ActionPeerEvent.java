package model;

import java.awt.event.ActionEvent;

public class ActionPeerEvent extends ActionEvent {

    Object argument;
    PeerEvent eventType;

    public ActionPeerEvent(Object source, int id, PeerEvent event) {
        super(source, id, event.toString());
        this.eventType = event;
    }


    public Object getArgument() {
        return this.argument;
    }

    public void setArgument(Object obj) {
        this.argument = obj;
    }
}
