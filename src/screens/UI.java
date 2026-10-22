package screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

final class UI {
    static final Color BACKGROUND = new Color(8, 12, 28);
    static final Color PANEL = new Color(18, 27, 52, 235);
    static final Color ACCENT = new Color(70, 210, 255);
    static final Color MUTED = new Color(175, 190, 215);

    private UI() { }

    static void centered(Graphics2D g, String text, int centerX, int baselineY) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, centerX - metrics.stringWidth(text) / 2, baselineY);
    }

    static void panel(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(PANEL);
        g.fillRoundRect(x, y, width, height, 18, 18);
        g.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 150));
        g.drawRoundRect(x, y, width, height, 18, 18);
    }

    static void background(Graphics2D g, int width, int height) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, width, height);
    }

    static void title(Graphics2D g, String text, int centerX, int baselineY) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
        g.setColor(Color.WHITE);
        centered(g, text, centerX, baselineY);
        g.setColor(ACCENT);
        g.fillRoundRect(centerX - 90, baselineY + 16, 180, 3, 3, 3);
    }
}
