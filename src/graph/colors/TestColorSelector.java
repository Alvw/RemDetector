package graph.colors;


import java.awt.*;

public class TestColorSelector implements ColorSelector {
    @Override
    public Color getColor(int index) {
        if(index%100 < 4) {
            return Color.RED;
        }
        return Color.YELLOW;
    }
}
