import java.awt.*;
import javax.swing.*;

/** A simple ring that fills up to the given percentage, colored green/orange/red vs a pass threshold. */
public class CircleProgressPanel extends JPanel {

    private final double percentage;
    private final double passThreshold;

    public CircleProgressPanel(double percentage, double passThreshold) {
        this.percentage = percentage;
        this.passThreshold = passThreshold;
        setPreferredSize(new Dimension(200, 200));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 20;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        int strokeWidth = 16;

        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(230, 230, 230));
        g2.drawOval(x, y, size, size);

        Color arcColor = percentage >= passThreshold ? new Color(60, 180, 90) : new Color(220, 90, 90);
        g2.setColor(arcColor);
        double angle = 360.0 * (percentage / 100.0);
        g2.drawArc(x, y, size, size, 90, -(int) Math.round(angle));

        String text = String.format("%.0f%%", percentage);
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(text, getWidth() / 2 - textWidth / 2, getHeight() / 2 + fm.getAscent() / 3);
    }
}
