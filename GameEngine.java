import entities.Ball;
import entities.Brick;
import entities.Paddle;
import levels.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NOTE FOR KARTHIK: 
  This is currently a monolithic skeleton containing the game loop, 
  input handling, and rendering. You can extract GameLoop, GameManager, 
  and InputHandler into the core package as per the project doc.
 */
public class GameEngine extends JPanel implements ActionListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int FPS = 60;

    private Paddle paddle;
    private Ball ball;
    private final List<Brick> bricks = new ArrayList<>();
    private Timer timer;
    private Level currentLevel;

    // live key state (NOT direct movement) → smooth movement
    private boolean leftPressed, rightPressed, upPressed, downPressed;

    public GameEngine() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        currentLevel = new Level();
        spawnEntities();
        initInput();

        timer = new Timer(1000 / FPS, this);
        timer.start();
    }

    private void spawnEntities() {
        paddle = new Paddle(WIDTH, HEIGHT);
        ball = new Ball(WIDTH, HEIGHT);
        currentLevel.loadBricks(bricks, WIDTH);
    }

    private void initInput() {
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT)  leftPressed = true;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
                if (e.getKeyCode() == KeyEvent.VK_UP)    upPressed = true;
                if (e.getKeyCode() == KeyEvent.VK_DOWN)  downPressed = true;
            }
            @Override public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT)  leftPressed = false;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
                if (e.getKeyCode() == KeyEvent.VK_UP)    upPressed = false;
                if (e.getKeyCode() == KeyEvent.VK_DOWN)  downPressed = false;
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        paddle.setMovingLeft(leftPressed);
        paddle.setMovingRight(rightPressed);
        paddle.setMovingUp(upPressed);
        paddle.setMovingDown(downPressed);

        paddle.update();
        ball.update();

        // ---------------------------------------------------------------------
        // TODO (Karthik): Implement CollisionSystem here.
        // Interaction logic does not exist. you make that
        // You will need to calculate bounces and call b.hit() here.
        // ---------------------------------------------------------------------
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        for (Brick b : bricks) b.draw(g2);
        paddle.draw(g2);
        ball.draw(g2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Block Breaker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new GameEngine());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}