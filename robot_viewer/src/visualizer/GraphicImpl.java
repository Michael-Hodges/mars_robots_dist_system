package visualizer;

import java.awt.*;

public class GraphicImpl implements Graphics {

    java.awt.Graphics2D wrappedGraphics;

    public GraphicImpl(java.awt.Graphics graphics) {
        this.wrappedGraphics = (Graphics2D)graphics;
    }

    @Override
    public void drawCircle(int x, int y, int radius, java.awt.Color color) {
        this.wrappedGraphics.setColor(color);
        this.wrappedGraphics.fillOval(x, y, radius, radius);
    }

    @Override
    public void writeText(int x, int y, String msg) {
        wrappedGraphics.drawString(msg, x, y);
    }

    public void setCurrent(java.awt.Graphics graphics) {
        this.wrappedGraphics = (Graphics2D)graphics;
    }
}
