package entities;

import java.awt.Color;
import java.awt.Graphics2D;

public class Brick extends Entity {
    private boolean alive = true;

    public Brick(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    public boolean isAlive() { return alive; }

    public void hit() {
        alive = false;
    }

    @Override
    public void update() {
        // static for now
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(200, 60, 60)); // All bricks are red now
        g.fillRect((int) x, (int) y, (int) width, (int) height);
        g.setColor(Color.BLACK);
        g.drawRect((int) x, (int) y, (int) width, (int) height);
    }
}