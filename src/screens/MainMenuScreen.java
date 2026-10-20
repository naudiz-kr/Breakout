package screens;

import core.GameContext;
import core.GameField;
import core.GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class MainMenuScreen implements GameState {
    private final GameContext cntx;

    public MainMenuScreen(GameContext cntx) {
        this.cntx = cntx;
    }

    @Override
    public void enter() { }

    @Override
    public void exit() { }

    @Override
    public void update(double dt) {
        var in = cntx.input;
        
        // Listen for number keys to select the level
        if (in.wasPressed(KeyEvent.VK_1)) {
            cntx.changeState(new PlayScreen(cntx, 1));
        } else if (in.wasPressed(KeyEvent.VK_2)) {
            cntx.changeState(new PlayScreen(cntx, 2));
        } else if (in.wasPressed(KeyEvent.VK_3)) {
            cntx.changeState(new PlayScreen(cntx, 3));
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Fill background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameField.WIDTH, GameField.HEIGHT);

        // Draw Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("BREAKOUT", GameField.WIDTH / 2 - 110, 200);

        // Draw Instructions
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Select Level to Start:", GameField.WIDTH / 2 - 120, 350);
        
        g.setColor(Color.CYAN);
        g.drawString("Press 1 - Level 1", GameField.WIDTH / 2 - 100, 420);
        g.drawString("Press 2 - Level 2", GameField.WIDTH / 2 - 100, 470);
        g.drawString("Press 3 - Level 3", GameField.WIDTH / 2 - 100, 520);
    }
}