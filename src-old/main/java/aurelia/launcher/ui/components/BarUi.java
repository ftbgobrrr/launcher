package aurelia.launcher.ui.components;

import javax.swing.*;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;

public class BarUi extends MetalScrollBarUI {

    private JButton b = new JButton() {

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 0);
        }

    };

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r)
    {
        g.setColor(new Color(96, 110, 72, 254));
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r)
    {
        g.setColor(new Color(81, 66, 52, 254));
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return b;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return b;
    }
}