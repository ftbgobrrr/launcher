package auleria.launcher.ui.components;

import auleria.launcher.Launcher;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import launchit.formatter.adapter.LowerCaseEnumAdapter;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TextSlider extends JPanel {

    class News {

        private Long date;
        private String message;

        public Long getDate() {
            return date;
        }

        public String getMessage() {
            return message;
        }
    }

    private Timer timer;
    private List<News> list;

    private JLabel label;
    private int decalX;
    private int index = 0;

    public TextSlider(Rectangle bounds) {
        this.setSize(bounds.getSize());
        this.setLayout(null);
        this.setOpaque(false);

        Launcher.getInstance().getLaunchit().getExecutorService().execute(() -> {
            try {
                String json = IOUtils.toString(Launcher.getInstance().getLaunchit().getConfig().getNewsUrl(), StandardCharsets.UTF_8);
                list = new GsonBuilder()
                        .registerTypeAdapterFactory(new LowerCaseEnumAdapter())
                        .setPrettyPrinting()
                        .create()
                        .fromJson(
                                json,
                                new TypeToken<List<News>>(){}.getType()
                        );
                if (list.size() == 0)
                    return;

            } catch (IOException e) {
                e.printStackTrace();
                Launcher.getLogger().severe("impossible des charger les news");
                return;
            }

            label = new JLabel();
            label.setText(list.get(0).getMessage());
            label.setForeground(Color.WHITE);
            label.setBounds(getWidth(), 0,  label.getFontMetrics(label.getFont()).stringWidth(label.getText()), getHeight());
            this.add(label);

            decalX = getWidth();
            timer = new Timer(10, e -> {
                int width = label.getWidth();
                if (--decalX < -width) {
                    if (++index == list.size())
                        index = 0;
                    label.setText(list.get(index).getMessage());
                    width = label.getFontMetrics(label.getFont()).stringWidth(list.get(index).getMessage());
                    label.setSize(width, label.getHeight());
                    decalX = getWidth();
                }
                label.setLocation(decalX, 0);
                repaint(0, 0, getWidth(), getHeight());
                Toolkit.getDefaultToolkit().sync();
            });
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.start();

            this.setVisible(true);
        });
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag && !timer.isRunning())
            timer.start();
        else if (!aFlag) timer.stop();
        super.setVisible(aFlag);
    }
}
