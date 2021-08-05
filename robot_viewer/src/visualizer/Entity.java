package visualizer;

public interface Entity {
    void draw(Graphics graphics);
    void update(World world);
    void setPosition(int x, int y);
}