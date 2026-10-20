package entities;

import core.GameField;
import java.awt.Color;
import java.awt.Graphics2D;

public class VerticalLaser extends Entity {
    private double warningTimer = 1.0; // 1 second of red warning
    private double activeTimer = 0.5;  // Laser stays white for 0.5 seconds
    private boolean isActive = false;

    public VerticalLaser(double x) {
        // Spans vertically from the top wall to the bottom, 20px wide
        super(x, GameField.TOP, 20, GameField.HEIGHT - GameField.TOP);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void update(double dt) {
        if (!isActive) {
            warningTimer -= dt;
            if (warningTimer <= 0) {
                isActive = true;
            }
        } else {
            activeTimer -= dt;
        }
    }

    public boolean isFinished() {
        return isActive && activeTimer <= 0;
    }

    @Override
    public void draw(Graphics2D g) {
        if (!isActive) {
            // Warning phase: Semi-transparent Red
            g.setColor(new Color(255, 0, 0, 120));
            g.fillRect((int) x, (int) y, (int) width, (int) height);
            
            g.setColor(Color.RED);
            g.drawRect((int) x, (int) y, (int) width, (int) height);
        } else {
            // Active phase: Solid White
            g.setColor(Color.WHITE);
            g.fillRect((int) x, (int) y, (int) width, (int) height);
        }
    }
}