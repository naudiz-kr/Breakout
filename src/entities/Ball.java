package entities;

import java.awt.Color;
import java.awt.Graphics2D;

public class Ball extends Entity {
    private double dx = 4, dy = -4;
    private final int screenWidth, screenHeight;

    public Ball(int screenWidth, int screenHeight) {
        super(screenWidth / 2.0 - 8, screenHeight / 2.0 - 8, 16, 16);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public double getDx() { return dx; }
    public double getDy() { return dy; }

    @Override
    public void update() {
        x += dx;
        y += dy;

        // wall bounce (all 4 sides)
        if (x <= 0 || x + width >= screenWidth) dx = -dx;
        if (y <= 0 || y + height >= screenHeight) dy = -dy;
    }

    // Interaction logic (bouncing off paddle/bricks) 
    // is handled by Karthik in the CollisionSystem.

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval((int) x, (int) y, (int) width, (int) height);
    }
}