package graph;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GraphUI extends BasicScrollBarUI {
    private JScrollBar scrollBar;

    public GraphUI(JScrollBar scrollBar) {
        this.scrollBar = scrollBar;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        if (incrButton != null) {
            incrButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    //Increment button is clicked!
                    System.out.println("Increment button is clicked " + scrollBar.getValue());
                }
            });
        }
        if (decrButton != null) {
            decrButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //Decrement button is clicked!
                    System.out.println("Decrement button is clicked " + scrollBar.getValue());
                }
            });
        }
    }
}
