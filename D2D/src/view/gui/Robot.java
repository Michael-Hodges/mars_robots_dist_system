package view.gui;

interface Robot extends Entity {
    void move(int amount);
    void rotate(int degrees);
    void setColor(int r, int g, int b);
    void setLabel(String label);
}
