package entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Abstract base for all drawable, updatable game objects.
 * Uses double precision for smooth movement.
 */
public abstract class Entity {
    protected double x, y, width, height;

    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Called once per frame. */
    public abstract void update(double dt);

    /** Called once per frame from paintComponent. */
    public abstract void draw(Graphics2D g);

    /** Axis-aligned bounding box for collisions. */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setWidth(double width) { this.width = width; }
}