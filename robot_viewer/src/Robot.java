import java.awt.*;

public class Robot implements Entity {

    private int direction;
    private int speed;
    private int x;
    private int y;
    private int SIZE = 200;
    private Color color;

    public Robot() {
        this.color = Color.GREEN;
    }

    void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.drawCircle(x, y, SIZE, color);
    }

    public void update(World w) {
        this.x += 1;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
