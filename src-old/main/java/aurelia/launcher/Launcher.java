package aurelia.launcher;

import aurelia.launcher.ui.FontManager;
import aurelia.launcher.ui.frames.ConsoleFrame;
import aurelia.launcher.ui.frames.Frame;
import aurelia.launcher.ui.panel.MainPanel;
import aurelia.launcher.ui.frames.SettingsFrame;
import aurelia.launcher.ui.components.Notification;
import launchit.Launchit;
import launchit.LaunchitConfig;
import launchit.auth.error.YggdrasilError;
import launchit.auth.profile.LauncherProfiles;
import launchit.events.AuthEvent;
import launchit.formatter.Manifest;
import launchit.formatter.versions.Version;
import launchit.formatter.versions.VersionType;
import launchit.utils.FilesUtils;
import launchit.utils.UrlUtils;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Launcher extends Handler {

    private static Launcher instance;
    private Frame frame;
    private SettingsFrame settingsFrame;
    private Launchit launchit;
    private FontManager fontManager;
    private ConsoleFrame console;

    public void init() {
        try {
            launchit = new LaunchitConfig()
                    .setInstallFolder(FilesUtils.getInstallDir(".aurelia"))
                    .setLauncherName("aurelia")
                    .setManifestUrl("https://launcher-api.auleria-rp.net/manifest")
                    .setNewsUrl("https://launcher-api.auleria-rp.net/news")
                    .create();

            getLogger().addHandler(this);
            getLogger().info("Starting launcher...");
            launchit.getEventBus().register(this);
            fontManager = new FontManager();
            fontManager.init();
            frame = new Frame();
            console = new ConsoleFrame();
            LauncherProfiles profiles = launchit.getSessionManager().getLauncherProfiles();
            boolean network = UrlUtils.netIsAvailable(launchit);
            if (profiles.getSelectedProfile() != null && profiles.getProfiles().get(profiles.getSelectedProfile()) != null) {
                if (!network) {
                    if (profiles.getProfiles().get(profiles.getSelectedProfile()).getSettings().getVersion() == null) {
                        JOptionPane.showMessageDialog(null, "Aucun serveur trouvé. Aucune instalation trouvée. Impossible de passer en mode hors ligne", "Init Error", JOptionPane.ERROR_MESSAGE);
                        getLogger().severe("Aucun serveur trouvé. Aucune instalation trouvée. Impossible de passer en mode hors ligne");
                        System.exit(0);
                        return;
                    }
                    getFrame().getMainPanel().getRouter().setCurrentRoute("play");
                    getLogger().severe("Aucun serveur trouvé. Passage en mode hors ligne");
                    SwingUtilities.invokeLater(() -> MainPanel.getNotif().sendNotif(Notification.NotificationType.ERROR, "Aucun serveur trouvé. Passage en mode hors ligne", 4, 4));
                } else {
                    launchit.getSessionManager().doRefresh(launchit.getSessionManager().getLauncherProfiles().getSelectedProfile());
                }
            } else getFrame().getMainPanel().getRouter().setCurrentRoute("login");
            getLogger().info("Authentication checked");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void selectDefaultVersion() {
        try {
            Manifest m = getLaunchit().getRemoteManifest();
            Version v = getLaunchit().getRemoteLatestVersion(m, VersionType.RELEASE);
            LauncherProfiles profiles = launchit.getSessionManager().getLauncherProfiles();
            if (profiles.getSelectedProfile() != null && profiles.getProfiles().get(profiles.getSelectedProfile()) != null) {
                profiles.getProfiles().get(profiles.getSelectedProfile()).getSettings().setVersion(v.getId());
                launchit.getSessionManager().saveProfiles();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Launcher.getLogger().severe("Erreur, Impossible de selectionner la version par default");
        }
    }


    @Subscribe
    public void refreshEvent(AuthEvent.Refresh event) {
        if (event.getError() instanceof YggdrasilError)
        {
            getLogger().severe(((YggdrasilError)event.getError()).getErrorMessage());
            MainPanel.getNotif().sendNotif(Notification.NotificationType.ERROR, ((YggdrasilError)event.getError()).getErrorMessage(), 4, 4);
            getFrame().getMainPanel().getRouter().setCurrentRoute("login");
            return;
        }

        if (event.getProfile() == null) {
            getLogger().severe("No profile... going back to login screen");
            getFrame().getMainPanel().getRouter().setCurrentRoute("login");
            return;
        }
        selectDefaultVersion();
        getFrame().getMainPanel().getRouter().setCurrentRoute("play");
    }

    public static void main(String[] args) {
        instance = new Launcher();
        instance.init();
    }

    public static Launcher getInstance() {
        return instance;
    }

    public Frame getFrame() {
        return frame;
    }

    public void openSettings() {
        settingsFrame = new SettingsFrame();
        settingsFrame.setVisible(true);
    }

    public SettingsFrame getSettingsFrame() {
        return settingsFrame;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public Launchit getLaunchit() {
        return launchit;
    }

    public ConsoleFrame getConsole() {
        return console;
    }

    public static Logger getLogger() {
        return getInstance().getLaunchit().getLogger();
    }

    @Override
    public void publish(LogRecord record) {
        String message = "[" + record.getLevel().getName() + "] " + record.getMessage();
        ConsoleFrame.getLauncherLogs().add(message);
        if (console != null && console.getSelect().getSelectedItem().equals("launcher")) {
            console.getArea().append(message + "\n");
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}
