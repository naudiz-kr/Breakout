package entities;

import core.GameField;
import java.awt.Color;
import java.awt.Graphics2D;

public class Laser extends Entity {
    private double warningTimer = 1.0; // 1 second of red warning
    private double activeTimer = 0.5;  // stays white for 0.5 seconds
    private boolean isActive = false;

    private Laser(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /** Beam spanning wall to wall at the given height, 20px tall. */
    public static Laser horizontal(double y) {
        return new Laser(GameField.LEFT, y, GameField.RIGHT - GameField.LEFT, 20);
    }

    /** Beam spanning top to bottom at the given x, 20px wide. */
    public static Laser vertical(double x) {
        return new Laser(x, GameField.TOP, 20, GameField.HEIGHT - GameField.TOP);
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isFinished() {
        return isActive && activeTimer <= 0;
    }

    @Override
    public void update(double dt) {
        if (!isActive) {
            warningTimer -= dt;
            if (warningTimer <= 0) isActive = true;
        } else {
            activeTimer -= dt;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!isActive) {
            g.setColor(new Color(255, 0, 0, 120));
            g.fillRect((int) x, (int) y, (int) width, (int) height);
            g.setColor(Color.RED);
            g.drawRect((int) x, (int) y, (int) width, (int) height);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect((int) x, (int) y, (int) width, (int) height);
        }
    }
}