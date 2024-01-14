package agh.ics.oop.render.renderer;

import agh.ics.oop.render.ImageOverlay;
import agh.ics.oop.render.UnitRenderer;
import agh.ics.oop.render.WorldRenderer;

public class ImageOverlayRenderer implements UnitRenderer<ImageOverlay> {
    @Override
    public void render(WorldRenderer renderer, ImageOverlay overlay) {
        renderer.putImageAtScreenCoords(
                overlay.screenPosition,
                overlay.imageKey,
                overlay.imageScale);
    }
}
