package entities;

import effects.Effect;
import java.awt.Color;
import java.awt.Graphics2D;

public class Pickup extends Entity {
    private final double fallSpeed = 150.0; // Pixels per second
    private final Effect effect;

    public Pickup(double x, double y, Effect effect) {
        // Pickups are 24x24 squares
        super(x, y, 24, 24); 
        this.effect = effect;
    }

    public Effect getEffect() {
        return effect;
    }

    /**
     * Returns true if the pickup has fallen off the bottom of the screen.
     * Karthik's engine can use this to remove the pickup from the active list.
     */
    public boolean isOffScreen() {
        return y > core.GameField.HEIGHT;
    }

    @Override
    public void update(double dt) {
        y += fallSpeed * dt;
    }

    @Override
    public void draw(Graphics2D g) {
        // Placeholder visual: Green for buffs, Red for debuffs
        // (Keerthana can replace this with an image later)
        if (effect.getName().toLowerCase().contains("shrink") || 
            effect.getName().toLowerCase().contains("debuff")) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.GREEN);
        }
        
        g.fillRect((int) x, (int) y, (int) width, (int) height);
        
        // Draw a white border 
        g.setColor(Color.WHITE);
        g.drawRect((int) x, (int) y, (int) width, (int) height);
    }
}