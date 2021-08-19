package view.gui;

/**
 * Graphics interface for drawing entities
 */
public interface Graphics {
    /**
     * Draws a circle on the wrapped Graphics object
     * @param x x coordinate of circle
     * @param y y coordinate of circle
     * @param radius radius of circle
     * @param color color to make circle
     */
    void drawCircle(int x, int y, int radius, java.awt.Color color);

    /**
     * Writes text to screen
     * @param x x coordinate of text
     * @param y y coordinate of text
     * @param msg message to write
     * @param color color to make text
     */
    void writeText(int x, int y, String msg, java.awt.Color color);

}
