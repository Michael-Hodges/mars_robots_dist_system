package view.gui;

/**
 * AN entity representation for the GUI
 */
public interface Entity {
    /**
     * Draws self with graphics
     * @param graphics graphics to draw on
     */
    void draw(Graphics graphics);

    /**
     * Updates self using the world
     * @param world World to update using
     */
    void update(World world);

    /**
     * Sets position of the entity
     * @param x x coordinate
     * @param y y coordinate
     */
    void setPosition(int x, int y);

    /**
     * Sets size of the entity
     * @param size size to set entity
     */
    void setSize(int size);
}
