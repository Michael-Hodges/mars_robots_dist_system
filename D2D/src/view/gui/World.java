package view.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    int width = 0;
    int height = 0;
    boolean isDirty = false;
    List<Entity> entities = new ArrayList<>();


    void addEntity(Entity entity) {
        entities.add(entity);
    }

    List<Entity> getEntities() {
        return entities;
    }

    public void setWorldDimensions(int width, int height) {
        isDirty = (width != this.width || height != this.height);
        this.width = width;
        this.height = height;
    }

    void update() {}
}
