package screens;

import core.GameContext;
import core.GameField;
import core.GameState;
import score.ScoreRepository;
import score.SqliteScoreRepository;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class NameEntryScreen implements GameState {
    private static final int MAX_LEN = 10;

    private final GameContext cntx;
    private final int level, score;
    private final Runnable onSaved, onCancel;
    private final ScoreRepository repo = new SqliteScoreRepository();
    private final StringBuilder name = new StringBuilder();
    private boolean confirming;

    public NameEntryScreen(GameContext cntx, int level, int score, Runnable onSaved, Runnable onCancel) {
        this.cntx = cntx;
        this.level = level;
        this.score = score;
        this.onSaved = onSaved;
        this.onCancel = onCancel;
    }

    @Override
    public void enter() {
        cntx.input.consumeTyped();
        name.setLength(0);
        confirming = false;
    }

    @Override public void exit() { }

    private void save() {
        repo.saveLevelScore(name.toString().trim(), level, score);
        onSaved.run();
    }

    @Override
    public void update(double dt) {
        var in = cntx.input;
        String typed = in.consumeTyped();

        if (confirming) {
            if (in.wasPressed(KeyEvent.VK_Y)) save();
            else if (in.wasPressed(KeyEvent.VK_N) || in.wasPressed(KeyEvent.VK_ESCAPE)) confirming = false;
            return;
        }
        if (in.wasPressed(KeyEvent.VK_ESCAPE)) {
            onCancel.run();
            return;
        }
        for (char c : typed.toCharArray()) {
            if (name.length() < MAX_LEN && (Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-'))
                name.append(c);
        }
        if (in.wasPressed(KeyEvent.VK_BACK_SPACE) && name.length() > 0)
            name.setLength(name.length() - 1);
        if (in.wasPressed(KeyEvent.VK_ENTER) && !name.toString().isBlank()) {
            if (repo.nameExists(name.toString().trim())) confirming = true;
            else save();
        }
    }

    @Override
    public void render(Graphics2D g) {
        UI.background(g, GameField.WIDTH, GameField.HEIGHT);
        int cx = GameField.WIDTH / 2;
        UI.title(g, "ENTER NAME", cx, 200);

        int pw = 420, ph = 240, px = cx - pw / 2, py = 250;
        UI.panel(g, px, py, pw, ph);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        UI.centered(g, "Level " + level + "  -  Score: " + score, cx, py + 50);

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
        g.setColor(UI.ACCENT);
        String cursor = (System.currentTimeMillis() / 500) % 2 == 0 ? "_" : " ";
        UI.centered(g, name + cursor, cx, py + 115);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        if (confirming) {
            g.setColor(Color.WHITE);
            UI.centered(g, "That name already exists. Same person?", cx, py + 165);
            UI.centered(g, "Y  -  Yes        N  -  No", cx, py + 195);
        } else {
            g.setColor(UI.MUTED);
            UI.centered(g, "Type a name  |  Enter - save  |  Esc - cancel", cx, py + 175);
        }
    }
}
