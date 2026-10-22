package entities;

import effects.Effect;
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
        boolean debuff = effect.getName().toLowerCase().contains("shrink")
                || effect.getName().toLowerCase().contains("debuff");
        g.drawImage(debuff ? SpriteFactory.DEBUFF_PICKUP : SpriteFactory.BUFF_PICKUP,
                (int) x, (int) y, (int) width, (int) height, null);
    }
}