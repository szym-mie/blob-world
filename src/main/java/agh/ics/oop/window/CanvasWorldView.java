package agh.ics.oop.window;

import agh.ics.oop.model.Boundary;
import agh.ics.oop.model.OutOfMapBoundsException;
import agh.ics.oop.model.Vector2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class CanvasWorldView implements WorldView {
    private final Canvas canvas;
    private final GraphicsContext contextContext;
    private WritableImage buffer;
    private PixelWriter bufferPixelWriter;
    private PixelReader bufferPixelReader;

    private Boundary gridBounds;
    private Float[] gridOffsetX;
    private Float[] gridOffsetY;
    private float imageScale;

    public CanvasWorldView(Canvas canvas) {
        this.canvas = canvas;
        this.contextContext = this.canvas.getGraphicsContext2D();
        this.registerSizeListener();
        this.gridBounds = new Boundary(new Vector2D(), new Vector2D());
    }

    private void createBuffer(float width, float height) {
        int bufferWidth = (int) Math.ceil(width);
        int bufferHeight = (int) Math.ceil(height);
        this.buffer = new WritableImage(bufferWidth, bufferHeight);
        this.bufferPixelWriter = this.buffer.getPixelWriter();
        this.bufferPixelReader = this.buffer.getPixelReader();
    }

    @Override
    public void setGridBounds(Boundary bounds) {
        this.gridBounds = bounds;
        this.updateViewSize();
    }

    @Override
    public void setGridBounds(Vector2D size) {
        this.setGridBounds(Boundary.fromSize(size));
    }

    @Override
    public void updateViewSize() {
        float width = (float) this.canvas.getWidth();
        float height = (float) this.canvas.getHeight();
        this.createBuffer(width, height);
        this.updateImageScale(width, height);
        this.updateGridOffsets(width, height);
    }

    @Override
    public void put(Vector2D position, Image image) throws OutOfMapBoundsException {
        this.checkIfInBounds(position);
        this.drawAtGrid(position, image);
    }

    @Override
    public Image get(Vector2D position) throws OutOfMapBoundsException {
        this.checkIfInBounds(position);
        return null;
    }

    @Override
    public void presentView() {
        this.contextContext.drawImage(this.buffer, 0, 0);
    }

    private void drawAtGrid(Vector2D position, Image image) {
        float x = this.gridOffsetX[position.getX()];
        float y = this.gridOffsetY[position.getY()];

        this.renderImage(image, x, y, this.imageScale);
    }

    private void renderImage(Image image, float x, float y, float imageScale) {
        PixelReader imagePixelReader = image.getPixelReader();
        float imageWidth = (float) image.getWidth();
        float imageHeight = (float) image.getHeight();
        float ex = x + imageScale;
        float ey = y + imageScale;
        float ix = 0f;
        float iy = 0f;
        float dx = imageWidth / imageScale;
        float dy = imageHeight / imageScale;

        for (float py = y; py < ey; py++) {
            for (float px = x; px < ex; px++) {
                Color c = imagePixelReader.getColor((int) ix, (int) iy);
                this.blendPixel((int) px, (int) py, c);
//                this.bufferPixelWriter.setColor((int) px, (int) py, c);
                ix += dx;
                if (ix >= 15f) ix = imageWidth - 1;
            }
            ix = 0;
            iy += dy;
            if (iy >= 15f) iy = imageHeight - 1;
        }
    }

    private void blendPixel(int x, int y, Color color) {
        Color blendColor = color;
        double opacity = color.getOpacity();
        if (opacity < 1) {
            Color prevColor = this.bufferPixelReader.getColor(x, y);
            blendColor = prevColor.interpolate(color, opacity);
        }
        this.bufferPixelWriter.setColor(x, y, blendColor);
    }

    private void checkIfInBounds(Vector2D position) throws OutOfMapBoundsException {
        if (!this.gridBounds.isInBounds(position))
            throw new OutOfMapBoundsException(position);
    }

    private void registerSizeListener() {
        this.canvas.widthProperty()
                .addListener((observable, previous_value, current_value) -> CanvasWorldView.this.updateViewSize());
        this.canvas.heightProperty()
                .addListener((observable, previous_value, current_value) -> CanvasWorldView.this.updateViewSize());
    }

    private void updateImageScale(float width, float height) {
        Vector2D gridSize = this.gridBounds.getSize();
        float imageWidth = width / gridSize.getX();
        float imageHeight = height / gridSize.getY();
        this.imageScale = Math.min(imageWidth, imageHeight);
    }

    private void updateGridOffsets(float width, float height) {
        Vector2D gridSize = gridBounds.getSize();
        float padX = width - gridSize.getX() * this.imageScale;
        float padY = height - gridSize.getY() * this.imageScale;
        float startX = padX / 2;
        float startY = padY / 2;

        this.gridOffsetX = this.gridBounds
                .mapColumns(x -> startX + x * this.imageScale)
                .toArray(Float[]::new);
        this.gridOffsetY = this.gridBounds
                .mapRows(y -> startY + y * this.imageScale)
                .toArray(Float[]::new);
    }
}
