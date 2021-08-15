package controller;

import java.awt.event.ActionEvent;

public class MessageEvent extends ActionEvent {

    MessageChannel channel;

    //extend to include isMulticast
    public MessageEvent(Object source, int id, MessageChannel channel, String message) {
        super(source, id, message);
        this.channel = channel;
    }

    public MessageChannel getChannel() {
        return this.channel;
    }
}