package core;

import javax.swing.JPanel;
import java.awt.*;

public class GameManager {
    public static final int WIDTH = GameField.WIDTH;
    public static final int HEIGHT = GameField.HEIGHT;

    private final GameContext context;
    private final GameLoop loop;
    private GameState current;
    private GameState pending;

    public GameManager() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        InputHandler input = new InputHandler();
        addKeyListener(input);
        context = new GameContext(this, input);
        loop = new GameLoop(this);
    }
    public GameContext getContext() {
        return context;
    }
    public void changeState(GameState next) {
        pending = next;
    }
    public void start() {
        requestFocusInWindow();
        loop.start();
    }
    public void stop() {
        loop.stop();
    }
    void update(double dt) {
        if(pending != null) {
            if(current != null)
                current.exit();
            current = pending;
            pending = null;
            current.enter();
        }
        if(current != null)
            current.update(dt);
        context.input.endFrame();
    }
    @Override
    protected void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if(current != null)
            current.render(g2);
    }
}
