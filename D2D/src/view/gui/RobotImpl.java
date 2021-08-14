package view.gui;

import model.Peer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class RobotImpl implements Entity, Robot {


    private int angle = 0; // in degrees
    private int speed;
    private int moveDistance;
    private int x;
    private int y;
    private int size = 200;
    private Color color;
    private Color upColor = Color.green;
    private Color downColor = Color.red;
    private Color leaderColor = Color.yellow;
    private Color textColor;
    private Random random = new Random();
    private String label = null;
    RobotStatus status;
    java.util.List<Robot> peers;

    public RobotImpl(String label, int x, int y) {
        this.label = label;
        this.color = Color.GREEN;
        this.x = x;
        this.y = y;
        this.angle = 0;
        this.speed = 10;
        this.peers = new ArrayList();
        this.status = RobotStatus.Up;
    }

    public void move(int amount) {
        this.moveDistance = amount;
    }

    public void rotate(int degrees) {
        this.angle += degrees;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public void setStatus(RobotStatus status) {
        this.status = status;
    }

    @Override
    public void addPeer(String label) {
        Robot peer = new RobotImpl(label,0,0);
        peer.setSize(this.size / 4);
        peer.setTextColor(0,0,0);
        this.peers.add(peer);
    }

    @Override
    public void removePeer(String label) {
        this.peers.remove(label);
    }

    @Override
    public void setPeerStatus(String peerLabel, RobotStatus status) {
        for(Robot p : peers) {
            if (p.getLabel().equals(peerLabel)) {
                p.setStatus(status);
            }
        }
    }

    @Override
    public void setColor(int r, int g, int b) {
        this.setColor(new Color(r,g,b));
    }

    @Override
    public void setTextColor(int r, int g, int b) {
        this.textColor = new Color(r, g, b);
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    Point toPolar(Point cartesianCoords) {
        double direction = Math.sqrt(cartesianCoords.x * cartesianCoords.x + cartesianCoords.y * cartesianCoords.y);
        double theta = 0;
        if (x != 0) {
            theta = Math.tanh(y / x);
        }

        return new Point((int)direction, (int)theta);
    }

    Point toCartesian(int r, int angle) {
        double theta = Math.toRadians(angle);
        int x = (int)(r * Math.cos(theta));
        int y = (int)(r * Math.sin(theta));
        return new Point(x, y);
    }

    public void draw(Graphics g) {
        Color c = color;
        switch(status) {
            case Up:
                c = this.upColor;
                break;
            case Down:
                c = this.downColor;
                break;
            case Leader:
                c = this.leaderColor;
                break;
            default:
                break;
        }

        g.drawCircle(x, y, size, c);
        if (this.label != null) {
            g.writeText(getCenterX(),getCenterY(), label, textColor);
        }
        int index = 0;
        for(Robot peer : peers) {
            Point p = calculatePeerPosition(index, peers.size(), peer.getSize());
            peer.setPosition(p.x, p.y);
            peer.draw(g);
            index++;
        }
    }

    private int getCenterX() {
        return x + (size / 2);
    }

    private int getCenterY() {
        return y + (size / 2);
    }

    public void update(World w) {
        if (this.moveDistance > 0) {
            Point delta = this.toCartesian(this.speed, this.angle);
            this.x += delta.x;
            this.y += delta.y;
            this.moveDistance -= this.speed;
        } else if (this.moveDistance < 0) {
            this.moveDistance = 0; //clamp to zero
        }
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private Point calculatePeerPosition(int peerIndex, int numberOfPeers, int peerSize) {
        int angleOffset = peerIndex * (360 / numberOfPeers) - 135;
        int distOffset = (int)(this.size * 0.75);
        Point offset = this.toCartesian(distOffset, angleOffset);
        return new Point(x + offset.x + (size / 2) - (peerSize / 2),
                y + offset.y + (size / 2) - (peerSize / 2));

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Robot)) {
            return false;
        }

        Robot r = (Robot) obj;

        return this.label.equals(r.getLabel());
    }
}
