import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicButtonUI;

/** Central visual theme for the Quiz Management System. */
public final class AppTheme {
    public static final Color BG = new Color(248, 246, 252);
    public static final Color CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(124, 92, 191);
    public static final Color PRIMARY_DARK = new Color(99, 70, 160);
    public static final Color ACCENT = new Color(232, 180, 220);
    public static final Color TEXT = new Color(45, 42, 52);
    public static final Color MUTED = new Color(112, 106, 122);
    public static final Color BORDER = new Color(225, 219, 235);
    public static final Color INPUT = new Color(252, 250, 255);

    private AppTheme() {}

    public static void apply() {
        // Use Nimbus when available; it is much cleaner than the old Metal L&F.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Fall back to the platform default if Nimbus cannot be installed.
        }

        FontUIResource font = new FontUIResource("SansSerif", Font.PLAIN, 14);
        FontUIResource bold = new FontUIResource("SansSerif", Font.BOLD, 14);
        FontUIResource heading = new FontUIResource("SansSerif", Font.BOLD, 18);

        UIManager.put("Panel.background", new ColorUIResource(BG));
        UIManager.put("OptionPane.background", new ColorUIResource(BG));
        UIManager.put("Label.foreground", new ColorUIResource(TEXT));
        UIManager.put("Label.font", font);

        UIManager.put("Button.font", bold);
        UIManager.put("Button.foreground", new ColorUIResource(Color.WHITE));
        UIManager.put("Button.background", new ColorUIResource(PRIMARY));
        UIManager.put("Button.select", new ColorUIResource(PRIMARY_DARK));
        UIManager.put("Button.focus", new ColorUIResource(ACCENT));
        UIManager.put("Button.border", javax.swing.BorderFactory.createEmptyBorder(10, 18, 10, 18));
        UIManager.put("Button.margin", new java.awt.Insets(8, 16, 8, 16));

        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("TextField.background", new ColorUIResource(INPUT));
        UIManager.put("PasswordField.background", new ColorUIResource(INPUT));
        UIManager.put("TextField.foreground", new ColorUIResource(TEXT));
        UIManager.put("PasswordField.foreground", new ColorUIResource(TEXT));

        UIManager.put("ComboBox.font", font);
        UIManager.put("ComboBox.background", new ColorUIResource(CARD));
        UIManager.put("ComboBox.foreground", new ColorUIResource(TEXT));

        UIManager.put("List.font", font);
        UIManager.put("List.background", new ColorUIResource(CARD));
        UIManager.put("List.foreground", new ColorUIResource(TEXT));
        UIManager.put("List.selectionBackground", new ColorUIResource(new Color(221, 207, 244)));
        UIManager.put("List.selectionForeground", new ColorUIResource(TEXT));

        UIManager.put("Table.font", font);
        UIManager.put("Table.foreground", new ColorUIResource(TEXT));
        UIManager.put("Table.background", new ColorUIResource(CARD));
        UIManager.put("Table.selectionBackground", new ColorUIResource(new Color(221, 207, 244)));
        UIManager.put("Table.selectionForeground", new ColorUIResource(TEXT));
        UIManager.put("TableHeader.font", bold);
        UIManager.put("TableHeader.foreground", new ColorUIResource(TEXT));
        UIManager.put("TableHeader.background", new ColorUIResource(new Color(239, 233, 248)));

        UIManager.put("RadioButton.font", font);
        UIManager.put("RadioButton.foreground", new ColorUIResource(TEXT));
        UIManager.put("RadioButton.background", new ColorUIResource(BG));
        UIManager.put("CheckBox.font", font);
        UIManager.put("CheckBox.foreground", new ColorUIResource(TEXT));
        UIManager.put("CheckBox.background", new ColorUIResource(BG));

        UIManager.put("ProgressBar.foreground", new ColorUIResource(PRIMARY));
        UIManager.put("ProgressBar.background", new ColorUIResource(new Color(231, 224, 242)));
        UIManager.put("ProgressBar.font", bold);

        UIManager.put("TitledBorder.titleFont", heading);
        UIManager.put("TitledBorder.titleColor", new ColorUIResource(PRIMARY_DARK));

        UIManager.put("ScrollPane.background", new ColorUIResource(BG));
        UIManager.put("Viewport.background", new ColorUIResource(BG));
        UIManager.put("ToolTip.background", new ColorUIResource(new Color(55, 48, 70)));
        UIManager.put("ToolTip.foreground", new ColorUIResource(Color.WHITE));

        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", bold);
    }
}
