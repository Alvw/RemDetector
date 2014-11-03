package gui;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class MarkerLabel extends JLabel {

    private Color  backgroundColor;
    private Dimension defaultDimension = new Dimension(10,10);

    public MarkerLabel() {
        setPreferredSize(defaultDimension);
        setOpaque(true);
        backgroundColor = getBackground();
    }

    public MarkerLabel(Dimension dimension) {
        this();
        setPreferredSize(dimension);
    }

    public MarkerLabel(Icon icon) {
        this();
        setIcon(icon);

    }

    public void setIcon(Icon icon) {
        if(icon != null){
            setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        }
        super.setIcon(icon);
    }

    public void setColor(Color color) {
        setBackground(color);
        setIcon(null);
    }

    public void setBackgroundColor(){
        setColor(backgroundColor);
    }
}
