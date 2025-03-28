package agh.ics.oop.render.renderer;

import agh.ics.oop.entities.Animal;
import agh.ics.oop.model.Vector2D;
import agh.ics.oop.render.UnitRenderer;
import agh.ics.oop.render.WorldRenderer;
import agh.ics.oop.render.image.ImageSampler;
import agh.ics.oop.view.ViewLayer;

public class AnimalRenderer implements UnitRenderer<Animal> {
    private final ImageSampler blob;
    private final ImageSampler bar0;
    private final ImageSampler bar1;
    private final ImageSampler bar2;
    private final ImageSampler bar3;
    private final ImageSampler bar4;

    public AnimalRenderer(WorldRenderer renderer) {
        this.blob = renderer.imageSamplerMap.getImageSampler("blob");
        this.bar0 = renderer.imageSamplerMap.getImageSampler("bar0");
        this.bar1 = renderer.imageSamplerMap.getImageSampler("bar1");
        this.bar2 = renderer.imageSamplerMap.getImageSampler("bar2");
        this.bar3 = renderer.imageSamplerMap.getImageSampler("bar3");
        this.bar4 = renderer.imageSamplerMap.getImageSampler("bar4");
    }

    @Override
    public void render(WorldRenderer renderer, ViewLayer viewLayer, Animal element) {
        Vector2D position = element.getPosition();
        renderer.view.putImageAtGrid(position, this.blob, viewLayer);
        ImageSampler bar = this.getEnergyBarImageSampler(element.getEnergy());
        renderer.view.putImageAtGrid(position, bar, viewLayer);
    }

    private ImageSampler getEnergyBarImageSampler(int energy) {
        return switch (energy) {
            case 0 -> this.bar0;
            case 1 -> this.bar1;
            case 2 -> this.bar2;
            case 3 -> this.bar3;
            default -> this.bar4;
        };
    }
}
