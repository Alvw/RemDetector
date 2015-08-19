package graph.colors;

import java.awt.Color;


public class MonoColorSelector implements ColorSelector {
    private Color color;

    public MonoColorSelector(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor(int index) {
        return color;
    }
}
