package auleria.launcher.ui.components.textfield;

import javax.swing.*;
import java.awt.*;

public class TextField extends JPanel {

    private Color textColor = Color.WHITE;
    private JTextField f;
    private boolean hasTexture = false;
    private String texture;
    private String focusTexture;
    private boolean hasFocusTexture = false;

    public TextField(int x, int y, int width, int height, String placeholder, boolean password) {
        this.setLayout(null);
        this.setOpaque(false);
        setBounds(x, y, width, height);
        if (!password)
            f = new PlaceholderTextField(placeholder);
        else
            f = new PlaceholderPassTextField( placeholder);
        f.setBounds(5, 0, getWidth() - 10, getHeight());
        f.setBackground(null);
        f.setOpaque(false);
        f.setBorder(null);
        f.setForeground(textColor);
        f.setCaretColor(Color.BLACK);
        this.add(f);
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        f.setForeground(textColor);
    }

    public void setTexture(String img) {
        texture = img;
        hasTexture = true;
    }

    public void setFocusTexture(String img) {
        focusTexture = img;
        hasFocusTexture = true;
    }

    public String getValue()
    {
        return f.getText();
    }

    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (!hasTexture) {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            Image img = new ImageIcon(getClass().getResource(this.texture)).getImage();
            if (f.isFocusOwner() && hasFocusTexture)
                img = new ImageIcon(getClass().getResource(this.focusTexture)).getImage();
            g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
        super.paintComponent(g);

    }

}
