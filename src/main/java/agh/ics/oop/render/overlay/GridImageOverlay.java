package agh.ics.oop.render.overlay;

import agh.ics.oop.model.Vector2D;
import agh.ics.oop.render.AssignRenderer;
import agh.ics.oop.render.ImageOverlay;
import agh.ics.oop.render.WorldRenderer;
import agh.ics.oop.render.renderer.GridImageOverlayRenderer;
import agh.ics.oop.reactive.Reactive;

@AssignRenderer(renderer = GridImageOverlayRenderer.class)
public class GridImageOverlay extends ImageOverlay {
    public Reactive<Vector2D> gridPosition;

    public GridImageOverlay(Vector2D gridPosition,
                            int depthIndex,
                            String imageKey) {
        super(new Vector2D(), depthIndex, imageKey, 1f);
        this.gridPosition = new Reactive<>(gridPosition);
    }

    @Override
    public void updateOnFrame(WorldRenderer renderer) {}
}
