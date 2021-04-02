package ftbgobrrr.launcher.ui.components;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JPanel {

    public double value;
    private JLabel vLabel;
    private JLabel label;

    public ProgressBar()
    {
        this.setLayout(null);
        this.setOpaque(false);
    }

    public void bindValueLabel(JLabel vLabel) {
        this.vLabel = vLabel;
    }

    public void bindLabel(JLabel label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (vLabel != null)
            vLabel.setVisible(aFlag);
        if (label != null)
            label.setVisible(aFlag);
        super.setVisible(aFlag);
    }

    public void setValue(double v) {
        if (vLabel != null)
            vLabel.setText(Math.round(v) + "%");
        value = v;
    }

    public void setvLabel(String l) {
        if (label != null)
            label.setText(l);
    }

    protected void paintComponent(Graphics g) {
        double barRectWidth = (this.getWidth() * this.getValue()) / 100;

        g.setColor(getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        int end = (int) barRectWidth;
        g.setColor(getForeground());
        g.fillRect(0, 0, end, this.getHeight());

        repaint();
    }
}
