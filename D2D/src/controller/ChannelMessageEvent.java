package controller;

import java.awt.event.ActionEvent;

public class ChannelMessageEvent extends ActionEvent {

    MessageChannel channel;
    public ChannelMessageEvent(Object source, int id, MessageChannel channel, String message) {
        super(source, id, message);
        this.channel = channel;
    }

    public MessageChannel getChannel() {
        return this.channel;
    }
}