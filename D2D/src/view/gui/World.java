package view.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * World where the entities are deployed
 */
public class World {
    int width = 0;
    int height = 0;
    boolean isDirty = false;
    List<Entity> entities = new ArrayList<>();


    /**
     * Add an entity to the world
     * @param entity entity to add to the world
     */
    void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Gets list of entities in the world
     * @return returns a List of entities
     */
    List<Entity> getEntities() {
        return entities;
    }

    /**
     * Sets the dimensions of the world
     * @param width width of the world
     * @param height height of the world
     */
    public void setWorldDimensions(int width, int height) {
        isDirty = (width != this.width || height != this.height);
        this.width = width;
        this.height = height;
    }

    /**
     * Super Secret method that does things
     */
    void update() {}
}
