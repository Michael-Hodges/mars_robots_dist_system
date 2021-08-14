package view.gui;

import java.awt.*;


public class GraphicImpl implements Graphics {

    Graphics2D wrappedGraphics;
    Font font = new Font("TimesRoman", Font.BOLD, 24);

    public GraphicImpl(java.awt.Graphics graphics) {
        this.wrappedGraphics = (Graphics2D)graphics;
        this.wrappedGraphics.setFont(font);
    }

    @Override
    public void drawCircle(int x, int y, int radius, Color color) {
        this.wrappedGraphics.setColor(color);
        this.wrappedGraphics.fillOval(x, y, radius, radius);
    }

    @Override
    public void writeText(int x, int y, String msg, Color color) {
        int width = this.wrappedGraphics.getFontMetrics().stringWidth(msg);
        this.wrappedGraphics.setColor(color);
        wrappedGraphics.drawString(msg, x - (width/2), y); //center text
    }

    public void setCurrent(java.awt.Graphics graphics) {
        this.wrappedGraphics = (Graphics2D)graphics;
        this.wrappedGraphics.setFont(font);
    }
}
