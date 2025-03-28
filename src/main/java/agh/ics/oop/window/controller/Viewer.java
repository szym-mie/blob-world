package agh.ics.oop.window.controller;

import agh.ics.oop.entities.Animal;
import agh.ics.oop.model.*;
import agh.ics.oop.render.RendererEngine;
import agh.ics.oop.render.TextOverlay;
import agh.ics.oop.render.WorldRenderer;
import agh.ics.oop.render.image.ImageAtlasSampler;
import agh.ics.oop.render.image.ImageMap;
import agh.ics.oop.render.overlay.BouncingImageOverlay;
import agh.ics.oop.render.overlay.GridImageOverlay;
import agh.ics.oop.render.overlay.StaticTextOverlay;
import agh.ics.oop.view.CanvasView;
import agh.ics.oop.view.ViewInput;
import agh.ics.oop.window.WindowController;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;

import java.util.List;

public class Viewer extends WindowController implements ObjectEventListener<WorldMap> {
    @FXML
    public Canvas canvas;
    @FXML
    public Slider delaySlider;

    private CanvasView canvasView;
    private ViewInput viewInput;
    private WorldRenderer worldRenderer;
    private RendererEngine rendererEngine;
    private WorldMap worldMap;
    private Simulation simulation;

    @Override
    public void start() {
        super.start();

        this.delaySlider.valueProperty()
                .addListener((observable, oldValue, newValue) ->
                        this.handleDelayUpdate(newValue.intValue()));

        this.initRenderer();
        this.initOverlays();
        this.startRenderer();
        this.initWorldMap();
        this.setupSimulation();
    }

    private void initRenderer() {
        this.canvasView = new CanvasView(this.canvas);
        this.viewInput = new ViewInput();
        this.viewInput.attach(this.canvasView);

        this.canvasView.getRoot().widthProperty()
                .bind(this.window.getRoot().widthProperty());
        this.canvasView.getRoot().heightProperty()
                .bind(this.window.getRoot().heightProperty().subtract(50));

        this.worldRenderer = new WorldRenderer(
                this.getBundleItem("image_map", ImageMap.class).orElseThrow(),
                this.canvasView
        );

        this.worldRenderer.imageSamplerMap.addFontAtlasSampler(
                "font0",
                "font0_atlas",
                new Vector2D(10, 16));

        this.worldRenderer.imageSamplerMap.addImageAtlasSampler(
                "planta",
                "plant_atlas",
                (image) -> new ImageAtlasSampler(image, new Vector2D(16, 16))
        );
    }

    private void initOverlays() {
        BouncingImageOverlay bouncyDVD =
                new BouncingImageOverlay(new Vector2D(50, 50), 4, "dvd0", 4f);
        bouncyDVD.setVelocity(new Vector2D(12, 8));
        this.worldRenderer.addOverlay("dvd", bouncyDVD);

        GridImageOverlay selectOverlay =
                new GridImageOverlay(new Vector2D(), 2, "sel0");
        this.worldRenderer.addOverlay("selector", selectOverlay);

        selectOverlay.gridPosition.bindTo(
                this.viewInput.mousePosition,
                this.canvasView::getGridIndex);

        GridImageOverlay targetOverlay =
                new GridImageOverlay(new Vector2D(), 3, "tgt0");
        this.worldRenderer.addOverlay("target", targetOverlay);

        targetOverlay.hide();

        TextOverlay frameTimeOverlay =
                new StaticTextOverlay(new Vector2D(0, 0), 1, "font0_atlas", 1f, "");
        this.worldRenderer.addOverlay("frame_time", frameTimeOverlay);

        frameTimeOverlay.text.bindTo(
                this.worldRenderer.frameRenderTime,
                (time) -> "frame_T [ms]: " + time / 1_000_000L);

        TextOverlay mapStatisticsOverlay =
                new StaticTextOverlay(new Vector2D(0, 32), 1, "font0_atlas", 1f, "");
        this.worldRenderer.addOverlay("map_statistics", mapStatisticsOverlay);
    }

    private void startRenderer() {
        this.rendererEngine = this.getBundleItem("renderer_engine", RendererEngine.class)
                .orElseThrow();

        this.rendererEngine.runRenderer(this.worldRenderer);
    }

    private void initWorldMap() {
        this.worldMap = this.getBundleItem("world_map", WorldMap.class)
                .orElseThrow();
        this.worldMap.addEventSubscriber(this);

        this.worldRenderer.setWorldMap(this.worldMap);
    }

    private void setupSimulation() {
        this.simulation = this.getBundleItem("simulation", Simulation.class)
                .orElseThrow();

        StatisticsCollector collector = new StatisticsCollector();
        StatisticsExporter exporter = new StatisticsExporter(this.worldMap.getTitle());
        collector.subscribeTo(this.worldMap);
        collector.addEventSubscriber(exporter);

        TextOverlay mapStatisticsOverlay = (TextOverlay) this.worldRenderer.getOverlay("map_statistics");
        collector.addEventSubscriber((statistics, message) ->
                mapStatisticsOverlay.text.setValue(statistics.toString()));

        GridImageOverlay selectOverlay = (GridImageOverlay) this.worldRenderer.getOverlay("selector");
        GridImageOverlay targetOverlay = (GridImageOverlay) this.worldRenderer.getOverlay("target");

        this.viewInput.isLeftMousePressed.addOnChange(isPressed -> {
            if (isPressed) {
                List<WorldElement> worldElementList =
                        this.worldMap.getElements(selectOverlay.gridPosition.getValue());
                Animal animal = (Animal) worldElementList.stream()
                        .filter(element -> element instanceof Animal)
                        .sorted()
                        .findFirst()
                        .orElse(null);
                if (animal != null) {
                    collector.setFocusedAnimal(animal);
                } else {
                    collector.unsetFocusedAnimal();
                }
            }
        });

        targetOverlay.isVisible.bindTo(collector.isAnimalFocused);
        targetOverlay.gridPosition.bindTo(collector.focusedAnimalPosition);

        this.window.setStageOnCloseRequest(event -> {
            this.simulation.kill();
            exporter.saveToFile();
        });
    }

    @Override
    public void sendEventData(WorldMap worldMap, String message) {
        if (message.equals("step")) {
            this.worldRenderer.setWorldMap(worldMap);
            rendererEngine.renderWorld(worldRenderer);
        }
    }

    @FXML
    private void handlePauseButtonClick() {
        if (this.simulation.isPaused())
            this.simulation.resume();
        else
            this.simulation.pause();
    }

    // set the amount of milliseconds to wait before subsequent step() calls
    @FXML
    private void handleDelayUpdate(int newDelay) {
        this.simulation.setUpdateDelay(newDelay);
    }
}
