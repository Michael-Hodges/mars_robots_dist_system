package controller;

import java.awt.event.ActionListener;

/**
 * Factory interface for producing messageListeners
 */
public interface MessageListenerFactory {
    /**
     * Return the message listener
     * @return new message listener
     */
    ActionListener getMessageListener();
}
