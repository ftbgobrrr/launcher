package aurelia.launcher.ui.components;


import org.apache.commons.lang3.ObjectUtils;

import javax.swing.*;
import java.awt.*;

public class ShadowLabel extends JLabel {

    public ShadowLabel(Rectangle bounds, Font font, String text, int horizontalAlignment) {
        super(text, null, horizontalAlignment);
        this.setLayout(null);
        this.setOpaque(false);
        this.setBounds(bounds);
        this.setForeground(new Color(0, 0, 0, 100));
        this.setFont(font);

        JLabel id = new JLabel(text, horizontalAlignment);
        id.setBounds(new Rectangle(3, 1, (int)bounds.getWidth(), (int)bounds.getHeight()));
        id.setForeground(Color.WHITE);
        id.setFont(font);
        this.add(id);
    }
}