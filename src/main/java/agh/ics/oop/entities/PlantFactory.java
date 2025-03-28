package agh.ics.oop.entities;

import agh.ics.oop.Configuration;
import agh.ics.oop.model.Vector2D;

import static agh.ics.oop.Configuration.Fields.PLANT_ENERGY;

public class PlantFactory {
    private final int plantEnergy;

    public PlantFactory(Configuration configuration) {
        this.plantEnergy = configuration.get(PLANT_ENERGY);
    }

    public Plant create(Vector2D position) {
        return this.create(position, true);
    }

    public Plant create(Vector2D position, boolean isEdible) {
        return new Plant(position, (isEdible ? 1 : -1) * this.plantEnergy);
    }
}
