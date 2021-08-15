package controller;

import java.awt.event.ActionEvent;

/**
 * Message events tell peers when a message has occurred, for use in an actionlistener.
 */
public class MessageEvent extends ActionEvent {

    MessageChannel channel;

    /**
     * Creates a new messageEvent with a given channel
     * @param source object which originated the event
     * @param id id of the event
     * @param channel channel that the event happened on
     * @param message message being passed in this event
     */
    public MessageEvent(Object source, int id, MessageChannel channel, String message) {
        super(source, id, message);
        this.channel = channel;
    }

    /**
     * Return the channel associated with the event
     * @return the channel associated with this event
     */
    public MessageChannel getChannel() {
        return this.channel;
    }
}