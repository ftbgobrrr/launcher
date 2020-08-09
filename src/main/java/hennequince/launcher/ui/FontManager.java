package hennequince.launcher.ui;

import hennequince.launcher.Launcher;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class FontManager {

    private static Font marcellus;

    public void init() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            InputStream d = getClass().getResourceAsStream("/fonts/marcellus.ttf");
            marcellus = Font.createFont(Font.TRUETYPE_FONT, d).deriveFont(22F);
            ge.registerFont(marcellus);
            setUIFont(new FontUIResource(marcellus));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            Launcher.getLogger().severe("impossible des charger les fonts");
        }
    }

    public void setUIFont(FontUIResource f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource)
                UIManager.put(key, f);
        }
    }

    public static Font getMarcellus() {
        return marcellus;
    }
}
