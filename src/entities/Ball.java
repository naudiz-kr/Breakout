package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import core.GameField;

public class Ball extends Entity {
    private double dx = 4, dy = -4;

    public Ball() {
        super (GameField.WIDTH / 2.0 - 4, 400, 8, 8 );
    }

    public double getDx() {
        return dx;
    }
    public double getDy() {
        return dy;
    }
    public void setVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void update() {
        x += dx;
        y += dy;

        if(x < GameField.LEFT) {
            x = GameField.LEFT;
            dx = Math.abs(dx);
        }
        if(x + width > GameField.RIGHT) {
            x = GameField.RIGHT - width;
            dx = -Math.abs(dx);
        }
        if(y < GameField.TOP) {
            y = GameField.TOP;
            dy = Math.abs(dy);
        }
        /* top, both sides, no bottom, which is fail*/

    }

    // Interaction logic (bouncing off paddle/bricks) 
    // is handled by Karthik in the CollisionSystem.

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval((int) x, (int) y, (int) width, (int) height);
    }
}