import core.GameManager;
import screens.LoadingScreen;
import javax.swing.*;

public class Main {
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BREAKOUT");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // --- FULLSCREEN ---
            // Maximizes the window
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

            GameManager manager = new GameManager();
            frame.add(manager);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setVisible(true);

            manager.changeState(new LoadingScreen(manager.getContext()));
            manager.start();
        });
    }
}
