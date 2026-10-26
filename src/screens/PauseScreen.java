package screens;

import core.GameContext;
import core.GameField;
import core.GameState;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class PauseScreen implements GameState {
    private final GameContext context;
    private final PlayScreen pausedGame;

    public PauseScreen(GameContext context, PlayScreen pausedGame) {
        this.context = context;
        this.pausedGame = pausedGame;
    }

    @Override
    public void enter() { }

    @Override
    public void exit() { }

    @Override
    public void update(double dt) {
        if (context.input.wasPressed(KeyEvent.VK_P)
                || context.input.wasPressed(KeyEvent.VK_ESCAPE)
                || context.input.wasPressed(KeyEvent.VK_ENTER)) {
            pausedGame.resume();
        } else if (context.input.wasPressed(KeyEvent.VK_M)) {
            context.changeState(new StartScreen(context));
        } else if (context.input.wasPressed(KeyEvent.VK_S) && pausedGame.getScore() > 0) {
            context.changeState(new NameEntryScreen(context,
                    pausedGame.getLevelNumber(), pausedGame.getScore(),
                    () -> context.changeState(new StartScreen(context)),
                    () -> context.changeState(this)));
        }
    }

    @Override
    public void render(Graphics2D g) {
        pausedGame.render(g);
        Graphics2D overlay = (Graphics2D) g.create();
        overlay.setComposite(AlphaComposite.SrcOver.derive(0.72f));
        overlay.setColor(Color.BLACK);
        overlay.fillRect(0, 0, GameField.WIDTH, GameField.HEIGHT);
        overlay.setComposite(AlphaComposite.SrcOver);

        int panelWidth = 380;
        int panelHeight = 290;
        int panelX = (GameField.WIDTH - panelWidth) / 2;
        int panelY = (GameField.HEIGHT - panelHeight) / 2;
        UI.panel(overlay, panelX, panelY, panelWidth, panelHeight);
        int centerX = GameField.WIDTH / 2;
        UI.title(overlay, "PAUSED", centerX, panelY + 78);

        overlay.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));
        overlay.setColor(Color.WHITE);
        UI.centered(overlay, "P / Esc / Enter  -  Resume", centerX, panelY + 145);
        UI.centered(overlay, "M  -  Return to main menu", centerX, panelY + 185);
        overlay.setColor(pausedGame.getScore() > 0 ? Color.WHITE : UI.MUTED);
        UI.centered(overlay, "S  -  Save & quit to menu", centerX, panelY + 225);
        overlay.dispose();
    }
}
