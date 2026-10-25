package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Brick extends Entity {
    private boolean alive = true;
    private final BufferedImage sprite;
    private final int points;

    public Brick(double x, double y, double w, double h, Color color, int points) {
        super(x, y, w, h);
        this.sprite = SpriteFactory.brick(color);
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
    public void update(double dt) {
        // static for now
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(sprite, (int) x, (int) y, (int) width, (int) height, null);
    }
}