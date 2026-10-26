package screens;

import core.GameContext;
import core.GameField;
import core.GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class ResultScreen implements GameState {
    private final GameContext cntx;
    private final int level, score;
    private final boolean won;
    private boolean saved;

    public ResultScreen(GameContext cntx, int level, int score, boolean won) {
        this.cntx = cntx;
        this.level = level;
        this.score = score;
        this.won = won;
    }

    private boolean canSave() { return !saved && score > 0; }
    private boolean hasNext() { return won && level < 3; }

    @Override public void enter() { }
    @Override public void exit() { }

    @Override
    public void update(double dt) {
        var in = cntx.input;
        if (in.wasPressed(KeyEvent.VK_R)) {
            cntx.changeState(new PlayScreen(cntx, level));
        } else if (in.wasPressed(KeyEvent.VK_M)) {
            cntx.changeState(new StartScreen(cntx));
        } else if (hasNext() && in.wasPressed(KeyEvent.VK_N)) {
            cntx.changeState(new PlayScreen(cntx, level + 1));
        } else if (canSave() && in.wasPressed(KeyEvent.VK_S)) {
            cntx.changeState(new NameEntryScreen(cntx, level, score,
                    () -> { saved = true; cntx.changeState(this); },
                    () -> cntx.changeState(this)));
        }
    }

    private static void line(Graphics2D g, String text, int cx, int y, boolean enabled) {
        g.setColor(enabled ? UI.ACCENT : UI.MUTED);
        UI.centered(g, text, cx, y);
    }

    @Override
    public void render(Graphics2D g) {
        UI.background(g, GameField.WIDTH, GameField.HEIGHT);
        int cx = GameField.WIDTH / 2;
        UI.title(g, won ? "LEVEL CLEAR" : "GAME OVER", cx, 200);

        int pw = 420, ph = 300, px = cx - pw / 2, py = 250;
        UI.panel(g, px, py, pw, ph);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        g.setColor(Color.WHITE);
        UI.centered(g, "Level " + level + "  -  Score: " + score, cx, py + 55);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        int y = py + 110;
        line(g, "R  -  Retry", cx, y, true);
        y += 40;
        if (hasNext()) {
            line(g, "N  -  Next level", cx, y, true);
            y += 40;
        }
        line(g, saved ? "S  -  Score saved" : "S  -  Input score", cx, y, canSave());
        y += 40;
        line(g, "M  -  Main menu", cx, y, true);
    }
}