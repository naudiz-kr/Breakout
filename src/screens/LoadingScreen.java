package screens;

import core.GameContext;
import core.GameField;
import core.GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class LoadingScreen implements GameState {
    private static final double MINIMUM_DISPLAY_SECONDS = 1.0;
    private static final int BAR_WIDTH = 320;
    private static final int BAR_HEIGHT = 16;

    private final GameContext context;
    private double elapsed;
    private boolean transitioned;

    public LoadingScreen(GameContext context) {
        this.context = context;
    }

    @Override
    public void enter() {
        elapsed = 0;
        transitioned = false;
    }

    @Override
    public void exit() { }

    @Override
    public void update(double dt) {
        if (transitioned) return;
        elapsed += dt;
        if (elapsed >= MINIMUM_DISPLAY_SECONDS) {
            transitioned = true;
            context.changeState(new StartScreen(context));
        }
    }

    @Override
    public void render(Graphics2D g) {
        UI.background(g, GameField.WIDTH, GameField.HEIGHT);
        int centerX = GameField.WIDTH / 2;
        UI.title(g, "BREAKOUT", centerX, 270);

        g.setColor(UI.MUTED);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        UI.centered(g, "Preparing the game...", centerX, 330);

        int x = centerX - BAR_WIDTH / 2;
        int y = 360;
        g.setColor(new Color(35, 45, 70));
        g.fillRoundRect(x, y, BAR_WIDTH, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);
        int fillWidth = (int) (BAR_WIDTH * Math.min(1, elapsed / MINIMUM_DISPLAY_SECONDS));
        g.setColor(UI.ACCENT);
        g.fillRoundRect(x, y, fillWidth, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, BAR_WIDTH, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);
    }
}
