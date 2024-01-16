package agh.ics.oop.window.controller;

import agh.ics.oop.entities.Animal;
import agh.ics.oop.entities.Plant;
import agh.ics.oop.model.MapChangeListener;
import agh.ics.oop.model.Vector2D;
import agh.ics.oop.model.WorldMap;
import agh.ics.oop.render.TextOverlay;
import agh.ics.oop.render.image.ImageAtlasSampler;
import agh.ics.oop.render.image.ImageMap;
import agh.ics.oop.render.ImageOverlay;
import agh.ics.oop.render.WorldRenderer;
import agh.ics.oop.render.overlay.BouncingImageOverlay;
import agh.ics.oop.render.overlay.StaticTextOverlay;
import agh.ics.oop.view.CanvasWorldView;
import agh.ics.oop.window.WindowController;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

public class Viewer extends WindowController implements MapChangeListener {
    @FXML
    public Canvas canvas;
    private WorldRenderer worldRenderer;
    private WorldMap worldMap;

    @Override
    public void start() {
        super.start();

        CanvasWorldView worldView = new CanvasWorldView(this.canvas);
        worldView.getRoot().widthProperty()
                .bind(this.window.getRoot().widthProperty());
        worldView.getRoot().heightProperty()
                .bind(this.window.getRoot().heightProperty());

        this.worldRenderer = new WorldRenderer(
                this.getBundleItem("image_map", ImageMap.class).orElseThrow(),
                worldView
        );

        this.worldRenderer.imageSamplerMap.addImageAtlasSampler(
                "font0",
                "font0_atlas",
                (image) -> new ImageAtlasSampler(image, new Vector2D(10, 16)));

        ImageOverlay testImageOverlay = new BouncingImageOverlay(new Vector2D(50, 50), "dvd0", 4f);
        this.worldRenderer.overlayList.add(testImageOverlay);

        TextOverlay testTextOverlay = new StaticTextOverlay(new Vector2D(10, 10), "font0_atlas", 4f, "abcdef");
        this.worldRenderer.overlayList.add(testTextOverlay);

        this.worldMap = this.getBundleItem("world_map", WorldMap.class).orElseThrow();
        this.worldMap.mapChangeSubscribe(this);

        this.worldRenderer.setWorldMap(this.worldMap);

        // Testing code
        Thread thread = new Thread(() -> {
            this.worldMap.placeElement(new Plant(new Vector2D(5, 5), 5));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            this.worldMap.placeElement(new Plant(new Vector2D(8, 5), 5));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            this.worldMap.placeElement(new Plant(new Vector2D(6, 7), 4));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            this.worldMap.placeElement(new Animal(new Vector2D(2, 2), 4, 0, 4, null));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            this.worldMap.placeElement(new Animal(new Vector2D(3, 3), 4, 0, 2, null));
        });
        thread.start();
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        this.worldRenderer.setWorldMap(worldMap);
        this.worldRenderer.renderView();
    }
}
