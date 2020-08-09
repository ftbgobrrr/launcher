package hennequince.launcher.ui.frames;

import hennequince.launcher.Launcher;
import launchit.auth.model.Profile;
import launchit.auth.profile.LauncherProfiles;
import launchit.utils.OperatingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

public class SettingsFrame extends JFrame implements ActionListener {

    private final JCheckBox console;
    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    public JButton submit;
    public JComboBox<Integer> ramc;
    public JTextField arguments;

    public SettingsFrame()
    {
        this.setIconImage(new ImageIcon(getClass().getResource("/images/icon.png")).getImage());
        this.setTitle("Settings");
        this.setResizable(false);

        this.setLocationRelativeTo(Launcher.getInstance().getFrame());

        JPanel options = new JPanel();
        options.setSize(250, 220);
        options.setPreferredSize(new Dimension(250, 220));
        options.setLayout(null);

        long deviceMemory = -1L;
        try {
            Method memoize = this.osBean.getClass().getDeclaredMethod("getTotalPhysicalMemorySize");
            memoize.setAccessible(true);
            deviceMemory = (Long) memoize.invoke(this.osBean) / 1024L / 1024L;
        } catch (Exception e) {
            e.printStackTrace();
        }

        LauncherProfiles profiles = Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles();
        Profile p = profiles.getProfiles().get(profiles.getSelectedProfile());

        JLabel raml = new JLabel();
        raml.setFont(new Font("Arial", Font.PLAIN, 12));
        raml.setBounds(options.getWidth() / 2 - 100, 10, 200, 15);
        raml.setForeground(Color.BLACK);
        raml.setText("Choisir la Ram (Mo) : ");
        options.add(raml);

        ramc = new JComboBox<>();
        ramc.setBounds(options.getWidth() / 2 - 100,30,200,30);
        ramc.addItem(512);
        ramc.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 1; i < (deviceMemory) / 1024L; i++)
            ramc.addItem(i * 1024);
        int ram = OperatingSystem.getArchMinRam();
        if(p.getSettings().getRam() != 0)
            ram = p.getSettings().getRam();
        ramc.setSelectedItem(ram);


        JLabel argsl = new JLabel();
        argsl.setFont(new Font("Arial", Font.PLAIN, 12));
        argsl.setBounds(options.getWidth() / 2 - 100, 75, 200, 15);
        argsl.setForeground(Color.BLACK);
        argsl.setText("Arguments : ");
        options.add(argsl);

        arguments = new JTextField();
        arguments.setText(p.getSettings().getArguments());
        arguments.setFont(new Font("Arial", Font.PLAIN, 12));
        arguments.setBounds(options.getWidth() / 2 - 100,95,200,30);

        console = new JCheckBox("Ouvrir la console au lancement");
        console.setFont(new Font("Arial", Font.PLAIN, 11));
        console.setBounds(options.getWidth() / 2 - 105,130,220,30);
        console.setSelected(p.getSettings().isConsoleAtStartup());
        options.add(console);

        options.add(arguments);
        options.add(ramc);

        submit = new JButton();
        submit.setText("Sauvegarder");
        submit.setFont(new Font("Arial", Font.PLAIN, 11));
        submit.addActionListener(this);
        submit.setBounds(options.getWidth() / 2  - 140 / 2, options.getHeight() - 40, 140, 30);

        options.add(submit);
        options.setVisible(true);
        this.add(options);
        pack();
    }


    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.setLocationRelativeTo(Launcher.getInstance().getFrame());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(submit)) {
            LauncherProfiles profiles = Launcher.getInstance().getLaunchit().getSessionManager().getLauncherProfiles();
            Profile p = profiles.getProfiles().get(profiles.getSelectedProfile());
            p.getSettings().setRam(ramc.getItemAt(ramc.getSelectedIndex()));
            p.getSettings().setArguments(arguments.getText());
            p.getSettings().setConsoleAtStartup(console.isSelected());
            Launcher.getInstance().getLaunchit().getSessionManager().saveProfiles();
            this.dispose();
        }
    }
}
