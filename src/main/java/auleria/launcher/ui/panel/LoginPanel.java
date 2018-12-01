package auleria.launcher.ui.panel;

import auleria.launcher.Launcher;
import auleria.launcher.ui.FontManager;
import auleria.launcher.ui.frames.Frame;
import auleria.launcher.ui.components.ImageButton;
import auleria.launcher.ui.components.Notification;
import auleria.launcher.ui.components.ShadowLabel;
import auleria.launcher.ui.components.textfield.TextField;
import auleria.launcher.ui.router.IRoute;
import launchit.auth.error.YggdrasilError;
import launchit.events.AuthEvent;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel implements IRoute, ActionListener {

    public ImageButton submit;

    public TextField loginField;
    public TextField passField;

    public LoginPanel() {
        Launcher.getInstance().getLaunchit().getEventBus().register(this);
    }

    @Override
    public void draw() {
        this.setLayout(null);
        this.setSize(270, 224);
        this.setLocation(Frame.getFrameSize().width - 323, 317);
        this.setOpaque(false);

        if (Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles().getProfiles().size() > 0) {
            Launcher.getInstance().getFrame().getMainPanel().getAccounts().setVisible(true);
            Launcher.getInstance().getFrame().getMainPanel().updatePos();
        }

        ShadowLabel id = new ShadowLabel(
                new Rectangle(0, 0, getWidth(), 40),
                FontManager.getDumbledor().deriveFont(30F),
                "Identifiant",
                SwingConstants.CENTER
        );
        this.add(id);

        loginField = new TextField(
                0,
                40,
                271,
                26,
                "",
                false
        );
        loginField.setTexture("/components/textField.png");
        loginField.setTextColor(Color.BLACK);
        this.add(loginField);

        ShadowLabel pass = new ShadowLabel(
                new Rectangle(0, 66, getWidth(), 40),
                FontManager.getDumbledor().deriveFont(30F),
                "Mot de passe",
                SwingConstants.CENTER
        );
        this.add(pass);

        passField = new TextField(
                0,
                110,
                271,
                26,
                "",
                true
        );
        passField.setTexture("/components/textField.png");
        passField.setTextColor(Color.BLACK);
        this.add(passField);

        submit = new ImageButton(new Rectangle(getWidth() / 2 - 145 / 2,getHeight() - 61, 145, 61));
        submit.setTextureHover("/components/login.png");
        submit.setTexture("/components/login.png");
        submit.setHoverFilter(new Color(200,200,200, 15));
        submit.addActionListener(this);
        this.add(submit);
    }

    @Override
    public void removeEvent() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(submit)) {
            if (loginField.getValue().isEmpty() || passField.getValue().isEmpty()) {
                MainPanel.getNotif().sendNotif(Notification.NotificationType.ERROR, "Un des champs est vide", 2, 3);
                return;
            }
            Launcher.getInstance().getLaunchit().getSessionManager().doLogin(loginField.getValue(), passField.getValue(), false);
        }
    }

    @Subscribe
    public void loginEvent(AuthEvent.Login event) {
        if (event.getError() instanceof YggdrasilError) {
            MainPanel.getNotif().sendNotif(Notification.NotificationType.ERROR, ((YggdrasilError)event.getError()).getErrorMessage(), 2, 3);
            return;
        }
        Launcher.getInstance().selectDefaultVersion();
        Launcher.getInstance().getFrame().getMainPanel().getRouter().setCurrentRoute("play");
    }
}
