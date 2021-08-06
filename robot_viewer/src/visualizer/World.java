package visualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    int WIDTH;
    int HEIGHT;
    List<Entity> entities = new ArrayList<>();
    static Random random = new Random();

    void addEntity(Entity entity) {
        entities.add(entity);
    }

    List<Entity> getEntities() {
        return entities;
    }

    void update() {}
}
