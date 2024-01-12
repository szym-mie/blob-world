package agh.ics.oop.render;

import agh.ics.oop.model.WorldElementA;

public class WorldElementARenderer extends WorldElementRenderer<WorldElementA> {
    @Override
    public void render(WorldRenderer renderer, WorldElementA element) {
        System.out.println("Render " + element.toString());
    }
}
