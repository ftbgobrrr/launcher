package hennequince.launcher.ui.panel;

import hennequince.launcher.Launcher;
import hennequince.launcher.ui.FontManager;
import hennequince.launcher.ui.components.*;
import hennequince.launcher.ui.frames.Frame;
import hennequince.launcher.ui.router.Router;
import launchit.auth.model.Settings;
import launchit.auth.profile.LauncherProfiles;
import launchit.events.DownloaderEvent;
import launchit.events.GameEvent;
import launchit.formatter.versions.Version;
import launchit.utils.OperatingSystem;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainPanel extends JPanel implements ActionListener {

    private final Router router;
    private final ImageButton accounts;
    private final TextSlider slider;
    private final MotionPanel grabPanel;
    private int decal;
    private Image bg;

    private ImageButton close;
    private ImageButton minimize;

    private static Notification notif;

    private ProgressBar progressBar;
    private ImageButton settings;
    private ImageButton console;

    public MainPanel(hennequince.launcher.ui.frames.Frame frame) {
        this.setPreferredSize(Frame.getFrameSize());
        this.setSize(hennequince.launcher.ui.frames.Frame.getFrameSize());
        this.setLayout(null);
        this.setOpaque(false);

        Launcher.getInstance().getLaunchit().getEventBus().register(this);

        grabPanel = new MotionPanel(frame);
        grabPanel.setOpaque(false);
        grabPanel.setVisible(OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS);
        this.add(grabPanel);

        decal = OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS ? 60 : 0;
        settings = new ImageButton(new Rectangle(getWidth() - 90 - decal,4, 28, 25));
        settings.setTextureHover("/components/settings.png");
        settings.setTexture("/components/settings.png");
        settings.setHoverFilter(new Color(200,200,200));
        settings.addActionListener(this);
        settings.setVisible(false);
        this.add(settings);

        console = new ImageButton(new Rectangle(getWidth() - 60 - decal,4, 28, 25));
        console.setTextureHover("/components/console.png");
        console.setTexture("/components/console.png");
        console.setHoverFilter(new Color(200,200,200));
        console.addActionListener(this);
        this.add(console);

        accounts = new ImageButton(new Rectangle(getWidth() - 30 - decal,4, 28, 25));
        accounts.setTextureHover("/components/accounts.png");
        accounts.setTexture("/components/accounts.png");
        accounts.setHoverFilter(new Color(200,200,200));
        accounts.addActionListener(this);
        accounts.setVisible(false);
        this.add(accounts);

        close = new ImageButton(new Rectangle(getWidth() - 30,2, 28, 28));
        close.setTextureHover("/components/close.png");
        close.setTexture("/components/close.png");
        close.setHoverFilter(new Color(200,200,200));
        close.addActionListener(this);
        close.setVisible(OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS);
        this.add(close);

        minimize = new ImageButton(new Rectangle(getWidth() - 60,2, 28, 28));
        minimize.setTextureHover("/components/minimize.png");
        minimize.setTexture("/components/minimize.png");
        minimize.setHoverFilter(new Color(200,200,200));
        minimize.addActionListener(this);
        minimize.setVisible(OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS);
        this.add(minimize);

        router = new Router();
        this.add(router);

        notif = new Notification();
        notif.setBounds(getWidth() / 2 - 150, 0, 300, getHeight());
        this.add(notif);

        slider = new TextSlider(new Rectangle(0, 0, getWidth() - 60 - decal, 30));
        this.add(slider);

        this.bg = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/bg.png"));


        JLabel value = new JLabel(
                "",
                SwingConstants.RIGHT
        );
        value.setFont(FontManager.getMarcellus().deriveFont(14F));
        value.setForeground(new Color(255, 255, 255, 255));
        value.setBounds(getWidth() - 45, getHeight() - 22, 40, 22);
        value.setVisible(false);
        this.add(value);

        JLabel label = new JLabel(
                "",
                SwingConstants.LEFT
        );
        label.setFont(FontManager.getMarcellus().deriveFont(14F));
        label.setForeground(new Color(255, 255, 255, 255));
        label.setBounds(5, getHeight() - 22, getWidth() - 50, 22);
        label.setVisible(false);
        this.add(label);

        progressBar = new ProgressBar();
        progressBar.bindValueLabel(value);
        progressBar.bindLabel(label);
        progressBar.setBackground(new Color(21, 24, 26, 140));
        progressBar.setForeground(new Color(44, 129, 45));
        progressBar.setBounds(0, getHeight() - 4, getWidth(), 4);
        progressBar.setVisible(false);
        this.add(progressBar);
        updatePos();
        this.setVisible(true);
    }


    public void updatePos() {
        decal = getWidth() - (OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS ? 60 : 0);

        int count = 0;
        count += settings.isVisible() ? 1 : 0;
        count += console.isVisible() ? 1 : 0;
        count += accounts.isVisible() ? 1 : 0;

        console.setBounds(new Rectangle(decal - count * 30,4, 28, 25));
        accounts.setBounds(new Rectangle(decal - count * 30 + 30,4, 28, 25));
        settings.setBounds(new Rectangle(decal - count * 30 + 60, 4, 28, 25));
        slider.setBounds(new Rectangle(0, 0, decal - count * 30, 30));
        grabPanel.setBounds(new Rectangle(0, 0, decal - count * 30, 30));
    }


    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.drawImage(bg, 0, 0, getWidth(), getHeight(),this);
        super.paintComponent(g);
    }

    public static Notification getNotif() {
        return notif;
    }

    public Router getRouter() {
        return router;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(close)) {
            System.exit(0);
        } else if (e.getSource().equals(minimize)) {
            Launcher.getInstance().getFrame().setState(JFrame.ICONIFIED);
        } else if (e.getSource().equals(settings)) {
            Launcher.getInstance().openSettings();
        } else if (e.getSource().equals(console)) {
            Launcher.getInstance().getConsole().setVisible(true);
        } else if (e.getSource().equals(accounts)) {
            Launcher.getInstance().getFrame().getMainPanel().getRouter().setCurrentRoute("accounts");
        }
    }

    @Subscribe
    public void deleteStart(DownloaderEvent.Delete.Pre event)
    {
        progressBar.setVisible(true);
        PlayPanel panel = ((PlayPanel)Router.getRoute("play"));
        panel.getPlay().setEnabled(false);
        progressBar.setvLabel("Deleting...");
        progressBar.setValue(((double)event.getCurrent() * 100) / (double)event.toDeleteCount());
    }

    @Subscribe
    public void checkFileStart(DownloaderEvent.Check.Pre event)
    {
        progressBar.setVisible(true);
        PlayPanel panel = ((PlayPanel)Router.getRoute("play"));
        panel.getPlay().setEnabled(false);
        progressBar.setvLabel("Checking...");
        progressBar.setValue(((double)event.getCurrent() * 100) / (double)event.toCheckCount());
        Launcher.getLogger().severe("Checking File: " + event.getArtifact().getPath());
    }

    @Subscribe
    public void checkFinished(DownloaderEvent.Check.Finished event)
    {
        progressBar.setValue(0);
        progressBar.setvLabel("");


        if (event.getFilesToDownload().size() == 0) {
            progressBar.setVisible(false);
            startGame(event.getVersion());
            return;
        }

        try {
            LauncherProfiles profiles = Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles();
            Version v = Launcher.getInstance().getLaunchit().getLocalVersion(profiles.getProfiles().get(profiles.getSelectedProfile()).getSettings().getVersion());
            Launcher.getInstance().getLaunchit().downloadFiles(v, event.getFilesToDownload());
        } catch (IOException e) {
            e.printStackTrace();
            Launcher.getLogger().severe("Erreur, Impossible de lire la version");
            MainPanel.getNotif().sendNotif(Notification.NotificationType.ERROR, "Erreur, Impossible de lire la version", 4, 4);
        }
    }

    @Subscribe
    public void downloadStart(DownloaderEvent.Download.Pre event) {
        progressBar.setVisible(true);
        PlayPanel panel = ((PlayPanel)Router.getRoute("play"));
        panel.getPlay().setEnabled(false);
        progressBar.setvLabel("Downloading...");
        Launcher.getLogger().severe("Downloading File: " + event.getDownloadable().getPath());
    }

    @Subscribe
    public void downloadFinished(DownloaderEvent.Download.Finished event) {
        progressBar.setValue(0);
        progressBar.setvLabel("");
        progressBar.setVisible(false);
        startGame(event.getVersion());
    }

    @Subscribe
    public void downloadProgress(DownloaderEvent.Download.Progess event) {
        progressBar.setValue(((double)event.getProgress().getProgress() * 100) / (double)event.getProgress().getTotal());
    }

    public void startGame(Version v) {
        PlayPanel panel = ((PlayPanel)Router.getRoute("play"));
        panel.getPlay().setEnabled(true);
        Launcher.getInstance().getLaunchit().getGameManager().startGame(v);
        Launcher.getInstance().getFrame().setState(java.awt.Frame.ICONIFIED);
        LauncherProfiles profiles = Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles();
        Settings settings = profiles.getProfiles().get(profiles.getSelectedProfile()).getSettings();
        if (settings.isConsoleAtStartup())
            Launcher.getInstance().getConsole().setVisible(true);
    }

    @Subscribe
    public void gameCloseEvent(GameEvent.Stop event) {
        Launcher.getInstance().getFrame().setState(java.awt.Frame.NORMAL);
        if (event.getExitCode() != 0) {
            Launcher.getInstance().getConsole().getSelect().setSelectedItem(event.getInstance().getName());
            Launcher.getInstance().getConsole().setVisible(true);

        }
    }

    public void setDecal(int decal) {
        this.decal = decal;
    }

    public ImageButton getSettings() {
        return settings;
    }

    public ImageButton getAccounts() {
        return accounts;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
