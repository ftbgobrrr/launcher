package aurelia.launcher.ui.components.textfield;

import aurelia.launcher.ui.FontManager;

import javax.swing.*;
import java.awt.*;

public class PlaceholderPassTextField extends JPasswordField {

    private String placeholder;

    public PlaceholderPassTextField(String placeholder2) {
        placeholder = placeholder2;
        setFont(FontManager.getDumbledor());
    }


    protected void paintComponent(Graphics pG) {
        super.paintComponent(pG);
        if ((this.placeholder.length() == 0) || (String.valueOf(getPassword()).length() > 0)) {
            return;
        }
        Graphics2D g = (Graphics2D) pG;
        g.setFont(FontManager.getDumbledor());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getForeground());
        g.drawString(this.placeholder, 0, getHeight() / 2 + 4);
    }
}
