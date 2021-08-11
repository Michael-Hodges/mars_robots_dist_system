package controller;

import model.SocketChannel;

import java.awt.event.ActionEvent;

public class TCPMessageEvent extends ActionEvent {

    SocketChannel channel;
    public TCPMessageEvent(Object source, int id, SocketChannel channel, String message) {
        super(source, id, message);
        this.channel = channel;
    }

    public SocketChannel getChannel() {
        return this.channel;
    }
}