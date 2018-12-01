package auleria.launcher.ui.panel;

import auleria.launcher.Launcher;
import auleria.launcher.ui.components.BarUi;
import auleria.launcher.ui.components.ImageButton;
import auleria.launcher.ui.frames.Frame;
import auleria.launcher.ui.router.IRoute;
import launchit.auth.model.Profile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

public class AccountsPanel extends JPanel implements IRoute, ActionListener, MouseListener {

    class AccountLabel extends JLabel {

        private String id;

        public AccountLabel(String s) {
            super(s);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    private ImageButton add;
    private ImageButton cancel;

    @Override
    public void draw() {
        this.setLayout(null);
        this.setSize(270, 224);
        this.setLocation(Frame.getFrameSize().width - 323, 317);
        this.setOpaque(false);

        JPanel listpane = new JPanel();
        listpane.setOpaque(false);
        listpane.setBorder(null);
        listpane.setBackground(new Color(0,0,0,0));
        listpane.setLayout(new BoxLayout(listpane, BoxLayout.Y_AXIS));

        Collection<Profile> profiles = Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles().getProfiles()
                .values();

        for (Profile p : profiles) {
            AccountLabel label = new AccountLabel(p.getName());
            label.setAlignmentX(LEFT_ALIGNMENT);
            label.addMouseListener(this);
            label.setForeground(Color.BLACK);
            label.setId(p.getId());
            listpane.add(label);
        }

        JScrollPane scrollable = new JScrollPane(listpane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollable.getViewport().setOpaque(false);
        scrollable.setBounds(0, 8, getWidth(), getHeight() - 80);
        scrollable.setBackground(new Color(0,0,0,0));
        scrollable.setOpaque(false);
        scrollable.setBorder(BorderFactory.createLineBorder(new Color(59, 51, 43, 230), 2));

        JScrollBar sb = scrollable.getVerticalScrollBar();
        sb.setBackground(new Color(0,0,0,0));
        sb.setPreferredSize(new Dimension(8, sb.getHeight()));
        sb.setOpaque(false);
        sb.setUI(new BarUi());
        this.add(scrollable);

        add = new ImageButton(new Rectangle(0,getHeight() - 61, 132, 61));
        add.setTextureHover("/components/add.png");
        add.setTexture("/components/add.png");
        add.setHoverFilter(new Color(200,200,200, 15));
        add.addActionListener(this);
        this.add(add);

        cancel = new ImageButton(new Rectangle(getWidth() - 132,getHeight() - 61, 132, 61));
        cancel.setTextureHover("/components/cancel.png");
        cancel.setTexture("/components/cancel.png");
        cancel.setHoverFilter(new Color(200,200,200, 15));
        cancel.addActionListener(this);
        this.add(cancel);

        this.add(scrollable);

        this.setVisible(true);
    }

    @Override
    public void removeEvent() {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(cancel)) {
            Launcher.getInstance().getFrame().getMainPanel().getRouter().setCurrentRoute("play");
        } else if (e.getSource().equals(add)) {
            Launcher.getInstance().getFrame().getMainPanel().getRouter().setCurrentRoute("login");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent() instanceof AccountLabel) {
            AccountLabel l = (AccountLabel) e.getComponent();
            Launcher.getInstance().getLaunchit().getSessionManager().doRefresh(l.getId());
            IRoute route = Launcher.getInstance().getFrame().getMainPanel().getRouter().getCurrent();
            if (route instanceof AccountsPanel)
                ((AccountsPanel) route).setVisible(false);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        e.getComponent().setForeground(Color.WHITE);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        e.getComponent().setForeground(Color.BLACK);
    }
}
