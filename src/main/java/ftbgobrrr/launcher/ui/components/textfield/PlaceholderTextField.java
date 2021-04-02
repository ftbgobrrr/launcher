package ftbgobrrr.launcher.ui.components.textfield;

import javax.swing.*;

import ftbgobrrr.launcher.ui.FontManager;

import java.awt.*;

public class PlaceholderTextField extends JTextField {

    private String placeholder;

    public PlaceholderTextField(String placeholder2) {
        placeholder = placeholder2;
        setFont(FontManager.getMarcellus());
    }

    protected void paintComponent(Graphics pG) {
        super.paintComponent(pG);
        if ((this.placeholder.length() == 0) || (getText().length() > 0)) {
            return;
        }
        Graphics2D g = (Graphics2D) pG;
        g.setFont(FontManager.getMarcellus());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getForeground());
        g.drawString(this.placeholder, 0, getHeight() / 2 + 4);
    }

}
