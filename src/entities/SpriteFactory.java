package entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

final class SpriteFactory {
    static final BufferedImage BALL = createBall();
    static final BufferedImage PADDLE = createPaddle();
    static final BufferedImage BUFF_PICKUP = createPickup(new Color(55, 220, 120));
    static final BufferedImage DEBUFF_PICKUP = createPickup(new Color(245, 75, 95));
    private static final Map<Integer, BufferedImage> BRICKS = new HashMap<>();

    private SpriteFactory() { }

    static BufferedImage brick(Color color) {
        return BRICKS.computeIfAbsent(color.getRGB(), ignored -> createBrick(color));
    }

    private static BufferedImage createBrick(Color color) {
        BufferedImage image = new BufferedImage(100, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        configure(g);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(2, 3, 96, 20, 7, 7);
        g.setPaint(new GradientPaint(0, 2, color.brighter(), 0, 21, color.darker()));
        g.fillRoundRect(2, 1, 96, 20, 7, 7);
        g.setColor(new Color(255, 255, 255, 150));
        g.setStroke(new BasicStroke(2));
        g.drawLine(9, 5, 91, 5);
        g.setColor(new Color(255, 255, 255, 75));
        g.drawRoundRect(2, 1, 95, 19, 7, 7);
        g.dispose();
        return image;
    }

    private static BufferedImage createBall() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        configure(g);
        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(2, 3, 13, 13);
        g.setPaint(new GradientPaint(3, 2, Color.WHITE, 13, 14, new Color(80, 190, 255)));
        g.fillOval(1, 1, 13, 13);
        g.setColor(new Color(255, 255, 255, 210));
        g.fillOval(4, 3, 4, 4);
        g.dispose();
        return image;
    }

    private static BufferedImage createPaddle() {
        BufferedImage image = new BufferedImage(140, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        configure(g);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(2, 4, 136, 18, 9, 9);
        g.setPaint(new GradientPaint(0, 2, new Color(125, 220, 255),
                0, 21, new Color(20, 78, 180)));
        g.fillRoundRect(2, 1, 136, 18, 9, 9);
        g.setColor(new Color(255, 255, 255, 190));
        g.setStroke(new BasicStroke(2));
        g.drawLine(12, 5, 128, 5);
        g.setColor(new Color(180, 235, 255, 160));
        g.drawRoundRect(2, 1, 135, 17, 9, 9);
        g.dispose();
        return image;
    }

    private static BufferedImage createPickup(Color color) {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        configure(g);
        int[] x = {16, 30, 16, 2};
        int[] y = {2, 16, 30, 16};
        int[] shadowY = {4, 18, 32, 18};
        g.setColor(new Color(0, 0, 0, 100));
        g.fillPolygon(x, shadowY, 4);
        g.setPaint(new GradientPaint(4, 4, color.brighter(), 28, 28, color.darker()));
        g.fillPolygon(x, y, 4);
        g.setColor(new Color(255, 255, 255, 210));
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(16, 10, 16, 22);
        if (color.getRed() > color.getGreen()) g.drawLine(10, 16, 22, 16);
        g.dispose();
        return image;
    }

    private static void configure(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
}
