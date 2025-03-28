package agh.ics.oop.view;

import agh.ics.oop.model.Vector2D;
import agh.ics.oop.reactive.Reactive;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class ViewInput {
    public Reactive<Boolean> isLeftMousePressed;
    public Reactive<Boolean> isRightMousePressed;
    public Reactive<Boolean> isInsideView;
    public Reactive<Vector2D> mousePosition;

    public ViewInput() {
        this.isLeftMousePressed = new Reactive<>(false);
        this.isRightMousePressed = new Reactive<>(false);
        this.isInsideView = new Reactive<>(false);
        this.mousePosition = new Reactive<>(new Vector2D());
    }

    public void attach(View<?> view) {
        Node node = view.getRoot();
        node.setOnMouseMoved(this::updatePosition);
        node.setOnMousePressed(this::updateButtons);
        node.setOnMouseReleased(this::updateButtons);
        node.setOnMouseEntered(this::updateMoveIn);
        node.setOnMouseExited(this::updateMoveOut);
    }

    public void detach(View<?> view) {
        Node node = view.getRoot();
        node.setOnMouseMoved(null);
        node.setOnMousePressed(null);
        node.setOnMouseReleased(null);
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
    }

    protected void updatePosition(MouseEvent event) {
        Vector2D position = new Vector2D((int) event.getX(), (int) event.getY());
        this.mousePosition.setValue(position);
    }

    protected void updateButtons(MouseEvent event) {
        isLeftMousePressed.setValue(event.isPrimaryButtonDown());
        isRightMousePressed.setValue(event.isSecondaryButtonDown());
    }

    protected void updateMoveIn(MouseEvent event) {
        this.isInsideView.setValue(true);
    }

    protected void updateMoveOut(MouseEvent event) {
        this.isInsideView.setValue(false);
    }
}
