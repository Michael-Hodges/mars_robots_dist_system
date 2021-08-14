package view.gui;

import javax.swing.*;

public class DashboardImpl extends JPanel implements Dashboard {

    GraphicImpl graphics;
    World world;

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

        for(Entity e : this.world.getEntities()) {
            e.draw(graphics);
        }
    }

    public void start() {
        Timer timer = new Timer(1000/60, (e) -> updateLoop());
        timer.start();
    }

    @Override
    public void addEntity(Entity e) {
        this.world.addEntity(e);
    }

    public void updateLoop() {
        for(Entity e : this.world.getEntities()) {
            e.update(this.world);
        }
        this.world.update();
        this.repaint();
    }

}
