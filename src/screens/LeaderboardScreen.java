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
import java.util.List;

public class LeaderboardScreen implements GameState {
    private final GameContext cntx;
    private List<ScoreRepository.PlayerRow> rows = List.of();

    public LeaderboardScreen(GameContext cntx) {
        this.cntx = cntx;
    }

    @Override
    public void enter() {
        rows = new SqliteScoreRepository().getTopPlayers(10);
    }

    @Override public void exit() { }

    @Override
    public void update(double dt) {
        var in = cntx.input;
        if (in.wasPressed(KeyEvent.VK_ESCAPE) || in.wasPressed(KeyEvent.VK_ENTER)
                || in.wasPressed(KeyEvent.VK_L) || in.wasPressed(KeyEvent.VK_M))
            cntx.changeState(new StartScreen(cntx));
    }

    @Override
    public void render(Graphics2D g) {
        UI.background(g, GameField.WIDTH, GameField.HEIGHT);
        int cx = GameField.WIDTH / 2;
        UI.title(g, "LEADERBOARD", cx, 110);

        int pw = 680, ph = 520, px = cx - pw / 2, py = 140;
        UI.panel(g, px, py, pw, ph);

        String[] head = {"#", "NAME", "LVL 1", "LVL 2", "LVL 3", "TOTAL"};
        int[] cols = {30, 80, 290, 370, 450, 550};
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.setColor(UI.ACCENT);
        for (int i = 0; i < head.length; i++) g.drawString(head[i], px + cols[i], py + 55);
        g.setColor(UI.MUTED);
        g.drawLine(px + 20, py + 68, px + pw - 20, py + 68);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));
        for (int i = 0; i < rows.size(); i++) {
            var r = rows.get(i);
            int y = py + 105 + i * 38;
            g.setColor(i == 0 ? Color.YELLOW : Color.WHITE);
            g.drawString(String.valueOf(i + 1), px + cols[0], y);
            g.drawString(r.name(), px + cols[1], y);
            for (int l = 0; l < 3; l++)
                g.drawString(r.levels()[l] > 0 ? String.valueOf(r.levels()[l]) : "-", px + cols[2 + l], y);
            g.drawString(String.valueOf(r.total()), px + cols[5], y);
        }
        if (rows.isEmpty()) {
            g.setColor(UI.MUTED);
            UI.centered(g, "No scores yet", cx, py + 200);
        }
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g.setColor(UI.MUTED);
        UI.centered(g, "Esc / Enter  -  Back", cx, py + 495);
    }
}