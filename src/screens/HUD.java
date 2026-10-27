package screens;

import core.GameField;
import effects.Effect;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

final class Hud {
    private Hud() { }

    static void draw(Graphics2D g, int levelNumber, int lives, int score, int highScore, List<Effect> effects) {
        int hudX = GameField.WIDTH + 20;
        int hudY = 50;
        int barWidth = 150;
        int barHeight = 20;

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString("LEVEL: " + levelNumber, hudX, hudY);
        g.drawString("LIVES: " + lives, hudX, hudY + 25);
        g.setColor(Color.YELLOW);
        g.drawString("SCORE: " + score, hudX, hudY + 50);
        g.setColor(Color.CYAN);
        g.drawString("HIGH:  " + highScore, hudX, hudY + 75);

        hudY += 120;
        for (Effect effect : effects) {
            boolean isDebuff = effect.getName().toLowerCase().contains("shrink");
            g.setColor(new Color(40, 40, 40));
            g.fillRect(hudX - 5, hudY - 15, barWidth + 10, 45);

            g.setColor(Color.WHITE);
            g.drawString(String.format("%s: %.1fs", effect.getName(),
                    Math.max(0, effect.getTimeRemaining())), hudX, hudY);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(hudX, hudY + 5, barWidth, barHeight);

            double percent = Math.clamp(effect.getTimeRemaining() / effect.getDuration(), 0, 1);
            g.setColor(isDebuff ? Color.RED : Color.GREEN);
            g.fillRect(hudX, hudY + 5, (int) (barWidth * percent), barHeight);
            hudY += 60;
        }
    }
}
