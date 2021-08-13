package view.gui;

import java.awt.*;
import java.util.Random;

public class RobotImpl implements Entity, Robot {

    private int angle = 0; // in degrees
    private int speed;
    private int moveDistance;
    private int x;
    private int y;
    private int SIZE = 20;
    private Color color;
    private Point targetLocation;
    private Random random = new Random();
    private String label = null;

    public RobotImpl(int x, int y, int angle) {
        this.color = Color.GREEN;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = 10;
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
    public void setColor(int r, int g, int b) {
        this.setColor(new Color(r,g,b));
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
        g.drawCircle(x, y, SIZE, color);
        if (this.label != null) {
            g.writeText(x,y, label);
        }
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
}
