package ftbgobrrr.launcher;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import launchit.Launchit;
import launchit.launcher.LauncherFile;
import launchit.launcher.events.ILauncherHandler;
import launchit.LaunchitConfig;
import launchit.auth.error.YggdrasilError;
import launchit.auth.profile.LauncherProfiles;
import launchit.events.AuthEvent;
import launchit.formatter.Manifest;
import launchit.formatter.versions.Version;
import launchit.formatter.versions.VersionType;
import launchit.utils.FilesUtils;
import launchit.utils.OperatingSystem;
import launchit.utils.UrlUtils;
import org.greenrobot.eventbus.Subscribe;

import ftbgobrrr.launcher.ui.FontManager;
import ftbgobrrr.launcher.ui.components.Notification;
import ftbgobrrr.launcher.ui.frames.ConsoleFrame;
import ftbgobrrr.launcher.ui.frames.Frame;
import ftbgobrrr.launcher.ui.frames.SettingsFrame;
import ftbgobrrr.launcher.ui.panel.MainPanel;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Launcher extends Handler implements ILauncherHandler {

    private static Launcher instance;
    private Frame frame;
    private SettingsFrame settingsFrame;
    private Launchit launchit;
    private FontManager fontManager;
    private ConsoleFrame console;
    private final File bootloaderFile;

    public Launcher(String bootloaderPath) {
        if (bootloaderPath != null)
            this.bootloaderFile = new File(bootloaderPath);
        else 
            this.bootloaderFile = null;
    }

    public void init() {
        try {
            launchit = new LaunchitConfig()
                    .setInstallFolder(FilesUtils.getInstallDir(".ftbgobrrrrr"))
                    .setLauncherName("FTB GO BRRRRRRR")
                    .setManifestUrl("http://localhost:3000/manifest")
                    .setNewsUrl("http://localhost:3000/news")
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
                        JOptionPane.showMessageDialog(null, "Aucun serveur trouv??. Aucune instalation trouv??e. Impossible de passer en mode hors ligne", "Init Error", JOptionPane.ERROR_MESSAGE);
                        getLogger().severe("Aucun serveur trouv??. Aucune instalation trouv??e. Impossible de passer en mode hors ligne");
                        System.exit(0);
                        return;
                    }
                    getFrame().getMainPanel().getRouter().setCurrentRoute("play");
                    getLogger().severe("Aucun serveur trouv??. Passage en mode hors ligne");
                    SwingUtilities.invokeLater(() -> MainPanel.getNotif().sendNotif(Notification.NotificationType.ERROR, "Aucun serveur trouv??. Passage en mode hors ligne", 4, 4));
                } else {
                    launchit.getSessionManager().doRefresh(launchit.getSessionManager().getLauncherProfiles().getSelectedProfile());
                }
            } else getFrame().getMainPanel().getRouter().setCurrentRoute("login");
            if (network && this.bootloaderFile != null) {
                try {
                    Launcher.getLogger().info("Checking Bootloader " + bootloaderFile.getCanonicalPath().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                launchit.getLauncherManager().setiLauncherHandler(this);
                launchit.getLauncherManager().checkForUpdate(LauncherFile.Type.BOOTLOADER, bootloaderFile);
            }
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
        
        instance = new Launcher(args.length == 1 ? args[0] : null);
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
        if (console != null && Objects.equals(console.getSelect().getSelectedItem(), "launcher")) {
            console.getArea().append(message + "\n");
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}

    @Override
    public void endChecking(LauncherFile file, boolean needUpdate) {
        if (needUpdate) {
            launchit.getLauncherManager().update(file, bootloaderFile);
        }
    }

    @Override
    public void startUpdate(LauncherFile file) {
        getFrame().getMainPanel().getProgressBar().setVisible(true);
        getFrame().getMainPanel().getProgressBar().setvLabel("Updating Bootloader...");
        Launcher.getLogger().info("Updating Bootloader");
    }

    @Override
    public void updateProgress(LauncherFile file, int current, int total) {
        getFrame().getMainPanel().getProgressBar().setValue(((double)current * 100) / (double)total);
    }

    @Override
    public void updateFinished(LauncherFile file, boolean error) {
        getFrame().getMainPanel().getProgressBar().setValue(0);
        getFrame().getMainPanel().getProgressBar().setvLabel("");
        getFrame().getMainPanel().getProgressBar().setVisible(false);
    }
}
