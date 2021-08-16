package view.gui;

/**
 * Dashboard for displaying entities.
 */
public interface Dashboard {
    /**
     * Starts the dashboard running
     */
    void start();

    /**
     * Adds an entity to the dashboard
     * @param e entity to add to the dashboard
     */
    void addEntity(Entity e);
}
