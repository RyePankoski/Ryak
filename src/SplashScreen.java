import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreen extends JWindow {
    private final int duration;
    private final Timer fadeTimer;
    private float opacity = 1.0f;

    public SplashScreen(Main mainApp, int duration) {
        this.duration = duration;

        JPanel content = getjPanel();

        JLabel titleLabel = new JLabel("Yup{++}", JLabel.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 80));
        titleLabel.setForeground(new Color(255, 255, 255));
        content.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Parser & Editor", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Monospaced", Font.PLAIN, 28));
        subtitleLabel.setForeground(new Color(180, 250, 180));
        content.add(subtitleLabel, BorderLayout.SOUTH);

        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        setSize(600, 400);
        setLocationRelativeTo(null);
        setContentPane(content);

        fadeTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    fadeTimer.stop();
                    dispose();
                    mainApp.show();
                }
                repaint();
            }
        });
    }

    private JPanel getjPanel() {
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));
                g2d.setColor(new Color(20, 20, 20));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        content.setLayout(new BorderLayout());

        content.setOpaque(false);
        return content;
    }

    public void showSplash() {
        setVisible(true);
        Timer timer = new Timer(duration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fadeTimer.start();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}