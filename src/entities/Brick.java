package entities;

import java.awt.Color;
import java.awt.Graphics2D;

public class Brick extends Entity {
    private boolean alive = true;
    private final Color color;
    private final int points;

    public Brick(double x, double y, double w, double h, Color color, int points) {
        super(x, y, w, h);
        this.color = color;
        this.points = points;
    }

    public boolean isAlive() {
        return alive;
    }
    public void hit() {
        alive = false;
    }
    public int getPoints() {
        return points;
    }

    @Override
    public void update() {
        // static for now
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color); //Brick colours vary with position.
        g.fillRect((int) x, (int) y, (int) width, (int) height);
    }
}