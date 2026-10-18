import core.GameManager;
import screens.PlayScreen;
import javax.swing.*;

public class Main {
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new Jframe("BREAKOUT! EX");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GameManager manager = new GameManager();
            frame.add(manager);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setVisible(true);

            manager.changeState(new PlayScreen(manager.getContext()));
            manager.start();
        });
    }
}
