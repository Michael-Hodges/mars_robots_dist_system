import javax.swing.*;

public class PlanetViewer extends JPanel {

    GraphicImpl graphics;
    World world;

    public PlanetViewer() {
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
        Robot r = new Robot();
        r.setPosition(100, 100);
        this.world.addEntity(r);

        Robot q = new Robot();
        q.setPosition(200,100);
        this.world.addEntity(q);
        Timer timer = new Timer(1000/60, (e) -> updateLoop());
        timer.start();
    }

    public void updateLoop() {
        for(Entity e : this.world.getEntities()) {
            e.update(this.world);
        }
        this.world.update();
        this.repaint();
    }

}
