package hennequince.launcher.ui.components;

import hennequince.launcher.ui.FontManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Notification extends JPanel {

    private static final long serialVersionUID = 1L;
    private int Ydecal;
    private NotificationType currentType;
    private String currentText = "";
    private int t;
    private List<Thread> threads = new ArrayList<Thread>();

    private static Image error_img = new ImageIcon(Notification.class.getResource("/components/notifications/error_icon.png")).getImage();
    private static Image info_img = new ImageIcon(Notification.class.getResource("/components/notifications/info_icon.png")).getImage();
    private static Image success_img = new ImageIcon(Notification.class.getResource("/components/notifications/success_icon.png")).getImage();

    public Notification() {
        setLayout(null);
        setOpaque(false);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        t = countStringMultiLineHeight(g2d, currentText, getWidth() - 75 - 35, 79, 2) + 8;
        if (t < 80)
            t = 80;
        g2d.setColor(new Color(54, 54, 54, 220));
        g2d.fillRect(0, Ydecal - t, getWidth(), t);
        g2d.setColor(Color.WHITE);

        drawStringMultiLine(g2d, currentText, getWidth() - 75 - 35, 75, Ydecal - t + 17);
        Image img = null;
        if (currentType == NotificationType.ERROR) {
            img = error_img;
            g2d.setColor(new Color(245, 47, 76, 150));
        } else if (currentType == NotificationType.INFO) {
            img = info_img;
            g2d.setColor(new Color(58, 159, 242, 150));
        } else if (currentType == NotificationType.SUCCESS) {
            img = success_img;
            g2d.setColor(new Color(95, 176, 98, 150));
        }

        g2d.fillRect(0, Ydecal - t + t - 3, getWidth(), 3);
        g2d.drawImage(img, 5, Ydecal - t + ((t / 2) - (64 / 2)) - 4, 64, 64, this);

    }

    public void sendNotif(NotificationType type, final String text, final long speed, final long time) {
        currentText = text;
        currentType = type;

        Notification context = this;
        Thread notif = new Thread(() -> {
            try {
                while (Ydecal < t) {
                    Ydecal++;
                    context.repaint();
                    Thread.sleep(speed);
                }
                Thread.sleep(time * 1000);
                while (Ydecal >= 0) {
                    Ydecal--;
                    context.repaint();
                    Thread.sleep(speed);
                }
            } catch (InterruptedException e) {}
            threads.remove(this);
        });
        notif.start();
        threads.add(notif);
    }

    public void cancelAllNotifs() {
        for (Thread t : threads)
            t.interrupt();
        Ydecal = 0;
        t = 0;
    }

    public void drawStringMultiLine(Graphics2D g, String text, int maxWidth, int x, int y) {

        FontMetrics m = g.getFontMetrics();
        g.setFont(FontManager.getMarcellus());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, g.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        if (m.stringWidth(text) < maxWidth) {
            g.drawString(text, x, y);
        } else {
            String[] words = text.split(" ");
            StringBuilder currentLine = new StringBuilder(words[0]);
            for (int i = 1; i < words.length; i++) {
                if (m.stringWidth(currentLine + words[i]) < maxWidth) {
                    currentLine.append(" ").append(words[i]);
                } else {
                    g.drawString(currentLine.toString(), x, y);
                    y += m.getHeight();
                    currentLine = new StringBuilder(words[i]);
                }
            }
            if (currentLine.toString().trim().length() > 0) {
                g.drawString(currentLine.toString(), x, y);
            }
        }
    }

    public int countStringMultiLineHeight(Graphics2D g, String text, int maxWidth, int x, int y) {
        FontMetrics m = g.getFontMetrics();
        g.setFont(FontManager.getMarcellus());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, g.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        if (m.stringWidth(text) < maxWidth) {
            y += m.getHeight();
        } else {
            String[] words = text.split(" ");
            StringBuilder currentLine = new StringBuilder(words[0]);
            for (int i = 1; i < words.length; i++) {
                if (m.stringWidth(currentLine + words[i]) < maxWidth) {
                    currentLine.append(" ").append(words[i]);
                } else {
                    y += m.getHeight();
                    currentLine = new StringBuilder(words[i]);
                }
            }
            if (currentLine.toString().trim().length() > 0) {
                y += m.getHeight();
            }
        }
        return y;
    }

    public static enum NotificationType {
        INFO, ERROR, SUCCESS;
    }

}
