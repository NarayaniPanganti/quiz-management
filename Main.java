import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        AppTheme.apply();
        DatabaseHelper.initializeDatabase();
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
