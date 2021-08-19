package view.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * JPanel implementation of the Dashboard interface
 */
public class DashboardImpl extends JPanel implements Dashboard {

    GraphicImpl graphics;
    World world;

    /**
     * Constructs a new Dashboard
     */
    public DashboardImpl() {
        this.graphics = null;
        this.world = new World();
    }


    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if (graphics == null) {
            this.graphics = new GraphicImpl(g);
        } else {
            this.graphics.setCurrent(g);
        }

        Dimension dimension = this.getSize();
        this.world.setWorldDimensions(dimension.width, dimension.height);

        if (world.isDirty) {
            refreshLayout();
        }
        for(Entity e : this.world.getEntities()) {
            e.draw(graphics);
        }
    }

    /**
     * Starts a timer to update entities after an interval
     */
    public void start() {
        Timer timer = new Timer(1000/60, (e) -> updateLoop());
        timer.start();
    }

    @Override
    public synchronized void addEntity(Entity e) {
        this.world.addEntity(e);
        refreshLayout();
    }

    /**
     * Refreshes the layout of the JPanel
     */
    void refreshLayout() {
        double entityScale = 0.5;
        List<Entity> entities = this.world.getEntities();
        int nGrid = (int)Math.ceil(Math.sqrt(entities.size()));
        int squareSize = (int)((this.world.width * 0.9) / nGrid);
        int squareMiddleOffset = (int)(squareDiagonal(squareSize) / 2);
        int entitySize = (int)(squareSize * entityScale);
        int entityOffset = (int)(squareDiagonal(entitySize) / 2);

        int i = 0;
        for(Entity e : entities) {
            int column = i % nGrid;
            int row = i / nGrid;
            int x = column * squareSize;
            int y = row * squareSize;
            x += squareMiddleOffset - entityOffset;
            y += squareMiddleOffset - entityOffset;
            e.setSize(entitySize);
            e.setPosition(x,y);
            i++;
        }
    }

    /**
     * Calculates diagonal of a square
     * @param size size of a square
     * @return the length of the diagonal
     */
    double squareDiagonal(int size) {
        return Math.sqrt((size * size) + (size * size));
    }

    /**
     * Updates each of the entities then repaints the JPanel
     */
    public void updateLoop() {
        for(Entity e : this.world.getEntities()) {
            e.update(this.world);
        }
        this.world.update();
        this.repaint();
    }

}
