package ftbgobrrr.launcher.ui.router;

import javax.swing.*;

import ftbgobrrr.launcher.ui.frames.Frame;
import ftbgobrrr.launcher.ui.panel.AccountsPanel;
import ftbgobrrr.launcher.ui.panel.LoginPanel;
import ftbgobrrr.launcher.ui.panel.PlayPanel;

import java.util.HashMap;
import java.util.Map;

public class Router extends JPanel {

    private static Map<String, IRoute> routes = new HashMap<>();
    private IRoute current = null;

    public Router() {
        this.setSize(Frame.getFrameSize());
        this.setLayout(null);
        this.setOpaque(false);
        loadRoutes();
    }

    public void loadRoutes() {
        routes.put("login", new LoginPanel());
        routes.put("play", new PlayPanel());
        routes.put("accounts", new AccountsPanel());
    }

    public void setCurrentRoute(String name) {
        routes.forEach((n, r) -> {
            if (r instanceof JComponent)
                ((JComponent) r).removeAll();
            r.removeEvent();
        });
        current = getRoute(name);
        current.draw();
        if (current instanceof JComponent)
            this.add((JComponent) current);
        repaint();
    }

    public IRoute getCurrent() {
        return current;
    }

    public static IRoute getRoute(String name) {
        return routes.get(name);
    }

    public static Map<String, IRoute> getRoutes() {
        return routes;
    }
}
