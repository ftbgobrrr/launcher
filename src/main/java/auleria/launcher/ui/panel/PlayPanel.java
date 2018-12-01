package auleria.launcher.ui.panel;

import auleria.launcher.Launcher;
import auleria.launcher.ui.FontManager;
import auleria.launcher.ui.frames.Frame;
import auleria.launcher.ui.components.ImageButton;
import auleria.launcher.ui.components.Notification;
import auleria.launcher.ui.components.ShadowLabel;
import auleria.launcher.ui.router.IRoute;
import launchit.auth.SessionManager;
import launchit.auth.error.YggdrasilError;
import launchit.auth.profile.LauncherProfiles;
import launchit.events.AuthEvent;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class PlayPanel extends JPanel implements IRoute, ActionListener {

    private ImageButton play;
    private ImageButton logout;


    public PlayPanel() {
        Launcher.getInstance().getLaunchit().getEventBus().register(this);
    }

    @Override
    public void draw() {
        this.setLayout(null);
        this.setSize(270, 224);
        this.setLocation(Frame.getFrameSize().width - 323, 317);
        this.setOpaque(false);

        Launcher.getInstance().getFrame().getMainPanel().getSettings().setVisible(true);
        Launcher.getInstance().getFrame().getMainPanel().getAccounts().setVisible(true);
        Launcher.getInstance().getFrame().getMainPanel().updatePos();

        LauncherProfiles profiles = Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles();

        ShadowLabel hello = new ShadowLabel(
                new Rectangle(0, getHeight() / 2 - 40, getWidth(), 40),
                FontManager.getDumbledor().deriveFont(30F),
                String.format("Bienvenue %s !", profiles.getProfiles().get(profiles.getSelectedProfile()).getName()),
                SwingConstants.CENTER
        );
        this.add(hello);

        play = new ImageButton(new Rectangle(0,getHeight() - 61, 132, 61));
        play.setTextureHover("/components/play.png");
        play.setTexture("/components/play.png");
        play.setHoverFilter(new Color(200,200,200, 15));
        play.addActionListener(this);
        this.add(play);

        logout = new ImageButton(new Rectangle(getWidth() - 132,getHeight() - 61, 132, 61));
        logout.setTextureHover("/components/logout.png");
        logout.setTexture("/components/logout.png");
        logout.setHoverFilter(new Color(200,200,200, 15));
        logout.addActionListener(this);
        this.add(logout);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void removeEvent() {
        Launcher.getInstance().getFrame().getMainPanel().getSettings().setVisible(false);
        Launcher.getInstance().getFrame().getMainPanel().getAccounts().setVisible(false);
        Launcher.getInstance().getFrame().getMainPanel().updatePos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(logout)) {
            Launcher.getInstance().getLaunchit().getSessionManager().doLogout(
                    Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles().getSelectedProfile()
            );
            Launcher.getLogger().info("Logging out current user");
        } else if (e.getSource().equals(play)) {
            LauncherProfiles profiles = Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles();
            Launcher.getInstance().getLaunchit().checkForUpdate(profiles.getProfiles().get(profiles.getSelectedProfile()).getSettings().getVersion());
            play.setEnabled(false);
        }
    }

    @Subscribe
    public void logoutEvent(AuthEvent.Logout event) {
        if (event.getError() instanceof YggdrasilError) {
            MainPanel.getNotif().sendNotif(Notification.NotificationType.ERROR, ((YggdrasilError)event.getError()).getErrorMessage(), 2, 3);
            return;
        }
        Launcher.getLogger().info(event.getProfile().getName() + " has been logged out");

        SessionManager sm = Launcher.getInstance().getLaunchit().getSessionManager();
        Set<String> profileMap = Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles().getProfiles().keySet();
        if (profileMap.size() > 0) {
            sm.doRefresh((String) profileMap.toArray()[0]);
        } else Launcher.getInstance().getFrame().getMainPanel().getRouter().setCurrentRoute("login");
    }

    public ImageButton getPlay() {
        return play;
    }
}
