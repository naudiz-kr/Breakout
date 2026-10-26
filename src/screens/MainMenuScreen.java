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
        } else if (in.wasPressed(KeyEvent.VK_L)) {
        cntx.changeState(new LeaderboardScreen(cntx));
        }
    }

    @Override
    public void render(Graphics2D g) {
        UI.background(g, GameField.WIDTH, GameField.HEIGHT);
        int centerX = GameField.WIDTH / 2;
        UI.title(g, "BREAKOUT", centerX, 170);

        int panelWidth = 400;
        int panelX = centerX - panelWidth / 2;
        int panelY = 220;
        UI.panel(g, panelX, panelY, panelWidth, 260);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 21));
        g.setColor(Color.WHITE);
        UI.centered(g, "SELECT A LEVEL", centerX, panelY + 45);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        g.setColor(UI.ACCENT);
        UI.centered(g, "1   Level 1", centerX, panelY + 95);
        UI.centered(g, "2   Level 2", centerX, panelY + 140);
        UI.centered(g, "3   Level 3", centerX, panelY + 185);
        UI.centered(g, "L   Leaderboard", centerX, panelY + 230);

        int legX = centerX - 300, legY = 500;
        UI.panel(g, legX, legY, 600, 190);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.setColor(Color.WHITE);
        UI.centered(g, "CONTROLS", centerX, legY + 35);
        String[] keys = {
                "MENU:   1 / 2 / 3 - start level   |   L - leaderboard",
                "GAME:   Arrow keys - move paddle   |   P / Esc - pause",
                "PAUSED:   P / Esc / Enter - resume   |   S - save & quit   |   M - menu",
                "RESULT:   R - retry   |   N - next level   |   S - input score   |   M - menu"
        };
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g.setColor(UI.MUTED);
        for (int i = 0; i < keys.length; i++)
            g.drawString(keys[i], legX + 30, legY + 70 + i * 30);
    }
}