package agh.ics.oop.render;

import agh.ics.oop.model.Vector2D;
import agh.ics.oop.render.renderer.ImageOverlayRenderer;
import agh.ics.oop.reactive.Reactive;

@AssignRenderer(renderer = ImageOverlayRenderer.class)
public abstract class ImageOverlay extends Overlay {
    public Reactive<String> samplerKey;
    public Reactive<Float> scale;

    public ImageOverlay(Vector2D screenPosition,
                        int depthIndex,
                        String samplerKey,
                        float scale) {
        super(screenPosition, depthIndex);
        this.samplerKey = new Reactive<>(samplerKey);
        this.scale = new Reactive<>(scale);
    }
}
