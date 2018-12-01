package auleria.launcher.ui.frames;

import auleria.launcher.Launcher;
import launchit.events.GameEvent;
import launchit.game.GameInstance;
import launchit.utils.PasteUtils;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConsoleFrame extends JFrame implements ItemListener, ActionListener {

    private static final Dimension size = new Dimension(700, 500);
    private static List<String> launcherLogs = new ArrayList<>();
    private final JComboBox<String> select;
    private final JButton close;
    private final JButton upload;
    private final JScrollPane scrollable;

    private JTextArea area;

    public ConsoleFrame() {
        this.setIconImage(new ImageIcon(getClass().getResource("/images/icon.png")).getImage());
        this.setTitle("Console");
        this.setResizable(false);
        this.setLocationRelativeTo(Launcher.getInstance().getFrame());

        Launcher.getInstance().getLaunchit().getEventBus().register(this);

        JPanel console = new JPanel();
        console.setSize(size);
        console.setPreferredSize(size);
        console.setLayout(null);


        close = new JButton("Fermer");
        close.setBounds(5, 5, 80, 20);
        close.setFont(new Font("Arial", Font.PLAIN, 12));
        close.addActionListener(this);
        console.add(close);

        upload = new JButton("Upload les logs");
        upload.setBounds(90, 5, 150, 20);
        upload.setFont(new Font("Arial", Font.PLAIN, 12));
        upload.addActionListener(this);
        console.add(upload);

        select = new JComboBox<>();
        select.setBounds(console.getWidth() - 125, 5, 120, 20);
        select.addItem("launcher");
        select.setFont(new Font("Arial", Font.PLAIN, 12));
        select.addItemListener(this);
        console.add(select);

        close.setEnabled(!Objects.equals(select.getSelectedItem(), "launcher"));

        area = new JTextArea();
        area.setBackground(Color.BLACK);
        area.setForeground(Color.LIGHT_GRAY);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setText(String.join("\n", getLauncherLogs()) + "\n");

        area.setEditable(false);

        scrollable = new JScrollPane(area);
        scrollable.setBounds(5, 30, console.getWidth() - 10, console.getHeight() - 35);
        scrollable.getVerticalScrollBar().setValue(scrollable.getVerticalScrollBar().getMaximum());
        console.add(scrollable);

        console.setVisible(true);
        this.add(console);
        pack();
    }

    @Subscribe
    public void gameStartEvent(GameEvent.Start event) {
        select.addItem(event.getInstance().getName());
        select.setSelectedItem(event.getInstance().getName());
        area.setText(null);
    }

    @Subscribe
    public void gameLogEvent(GameEvent.Log event) {
        if (event.getInstance().getName().equals(select.getSelectedItem())) {
            area.append(event.getLine() + "\n");
            SwingUtilities.invokeLater(() -> scrollable.getVerticalScrollBar().setValue(scrollable.getVerticalScrollBar().getMaximum()));
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        GameInstance inst = Launcher.getInstance().getLaunchit().getGameManager().getInstances()
                .stream()
                .filter(i -> i.getName().equals(e.getItem()))
                .findFirst()
                .orElse(null);
        close.setEnabled(!e.getItem().equals("launcher"));

        if (inst != null) {
            area.setText(null);
            area.append(String.join("\n", inst.getLogs()) + "\n");
        } else if (e.getItem().equals("launcher")) {
            area.setText(null);
            area.append(String.join("\n", getLauncherLogs()) + "\n");
        }
        scrollable.getVerticalScrollBar().setValue(scrollable.getVerticalScrollBar().getMaximum());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(close))
            select.removeItem(select.getSelectedItem());
        else if (e.getSource().equals(upload)) {
            upload.setEnabled(false);
            upload.setText("Uploading....");
            Launcher.getInstance().getLaunchit().getExecutorService().execute(() -> {
                String url = PasteUtils.post(area.getText());
                upload.setText("Upload les logs");
                upload.setEnabled(true);
                if (url != null) {
                    if (url.equals("MAX_LEN")) {
                        JOptionPane.showMessageDialog(null, "Impossible d'envoyer les logs. (Fichier trop gros)", "Upload Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    StringSelection selection = new StringSelection(url);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(new URI(url));
                        } catch (IOException | URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.setLocationRelativeTo(Launcher.getInstance().getFrame());
        SwingUtilities.invokeLater(() -> scrollable.getVerticalScrollBar().setValue(scrollable.getVerticalScrollBar().getMaximum()));
    }

    public static Dimension getFrameSize() {
        return size;
    }

    public static List<String> getLauncherLogs() {
        return launcherLogs;
    }

    public JTextArea getArea() {
        return area;
    }

    public JComboBox<String> getSelect() {
        return select;
    }
}
