package auleria.launcher.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImageButton extends JButton {

    private String textureHover;
    private String texture;
    private Rectangle imageBounds;
    private Color hoverFilter = Color.WHITE;

    public ImageButton(Rectangle size) {
        setModel(new DefaultButtonModel());
        addMouseListener(new MouseHandler());
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBackground(new Color(0,0,0,0));
        setBounds(size);
        imageBounds = new Rectangle(0,0, (int)size.getWidth(), (int)size.getHeight());
        setText("");
    }

    public void setTextureHover(String textureHover) {
        this.textureHover = textureHover;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public void setHoverFilter(Color hoverFilter) {
        this.hoverFilter = hoverFilter;
    }

    public Color getHoverFilter() {
        return hoverFilter;
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING));

        Image img = new ImageIcon(getClass().getResource(getModel().isRollover() ? this.textureHover : this.texture)).getImage();

        BufferedImage filtered = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ge = (Graphics2D) filtered.getGraphics();
        ge.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ge.setRenderingHint(RenderingHints.KEY_ANTIALIASING, g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
        ge.setColor(getModel().isRollover() ? getHoverFilter() : new Color(0,0,0,0));
        if (!isEnabled())
            ge.setColor(new Color(100, 100, 100, 220));
        ge.fillRect(imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height);
        ge.setComposite(AlphaComposite.DstAtop);
        ge.drawImage(img, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, this);
        g2d.drawImage(filtered, null, imageBounds.x, imageBounds.y);
        super.paintComponent(g2d);
    }

    public void setTextureBounds(int x, int y, int width, int height) {
        imageBounds.setBounds(x, y, width, height);
    }

    public class MouseHandler extends MouseAdapter {

        public void mouseEntered(MouseEvent e) {
            ImageButton.this.getModel().setRollover(true);
        }

        public void mouseExited(MouseEvent e) {
            ImageButton.this.getModel().setRollover(false);
        }

        public void mousePressed(MouseEvent e) {
            if (!isEnabled())
                return;
            ImageButton.this.getModel().setArmed(true);
            ImageButton.this.getModel().setPressed(true);
        }

        public void mouseReleased(MouseEvent e) {
            if (!isEnabled())
                return;
            ImageButton.this.getModel().setPressed(false);
            ImageButton.this.getModel().setArmed(false);
        }
    }
}