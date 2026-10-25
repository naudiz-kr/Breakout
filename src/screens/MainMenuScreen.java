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
        UI.background(g, GameField.WIDTH, GameField.HEIGHT);
        int centerX = GameField.WIDTH / 2;
        UI.title(g, "BREAKOUT", centerX, 200);

        int panelWidth = 400;
        int panelHeight = 260;
        int panelX = centerX - panelWidth / 2;
        int panelY = 280;
        UI.panel(g, panelX, panelY, panelWidth, panelHeight);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 21));
        g.setColor(Color.WHITE);
        UI.centered(g, "SELECT A LEVEL", centerX, panelY + 48);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        g.setColor(UI.ACCENT);
        UI.centered(g, "1   Level 1", centerX, panelY + 100);
        UI.centered(g, "2   Level 2", centerX, panelY + 150);
        UI.centered(g, "3   Level 3", centerX, panelY + 200);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g.setColor(UI.MUTED);
        UI.centered(g, "Arrow keys move the paddle  |  P or Esc pauses",
                centerX, GameField.HEIGHT - 38);
    }
}