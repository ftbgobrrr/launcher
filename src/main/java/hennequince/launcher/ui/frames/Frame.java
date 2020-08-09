package hennequince.launcher.ui.frames;

import hennequince.launcher.ui.panel.MainPanel;
import launchit.utils.OperatingSystem;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    private static Dimension size = new Dimension(950, 566);
    private MainPanel panel;

    public Frame() {
        this.setTitle("Hennequince - Launcher");

        this.setIconImage(new ImageIcon(getClass().getResource("/images/icon.png")).getImage());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setUndecorated(OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS);

        this.setResizable(false);

        panel = new MainPanel(this);
        this.add(panel);
        pack();

        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public MainPanel getMainPanel() {
        return panel;
    }

    public static Dimension getFrameSize() {
        return size;
    }
}
