package entities;

import java.awt.Graphics2D;
import core.GameField;
import audio.AudioManager;
import audio.SoundId;

public class Ball extends Entity {
    private static final double SPEED = GameField.BASE_SPEED;
    private double dx = SPEED / Math.sqrt(2), dy = -SPEED / Math.sqrt(2);

    public Ball() {
        super (GameField.WIDTH / 2.0 - 4, 400, 8, 6 );
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
    public void update(double dt) {
        x += dx * dt;
        y += dy * dt;

        if(x < GameField.LEFT) {
            x = GameField.LEFT;
            dx = Math.abs(dx);
            AudioManager.play(SoundId.WALL_BOUNCE);
        }
        if(x + width > GameField.RIGHT) {
            x = GameField.RIGHT - width;
            dx = -Math.abs(dx);
            AudioManager.play(SoundId.WALL_BOUNCE);
        }
        if(y < GameField.TOP) {
            y = GameField.TOP;
            dy = Math.abs(dy);
            AudioManager.play(SoundId.WALL_BOUNCE);
        }
        /* top, both sides, no bottom, which is fail,
        * and speed might cause tunneling so * scale is added*/

    }

    // Interaction logic (bouncing off paddle/bricks) 
    // is handled by Karthik in the CollisionSystem.

    @Override
    public void draw(Graphics2D g) {
        int spriteSize = (int) Math.ceil(Math.max(width, height) + 4);
        int spriteX = (int) Math.round(x + (width - spriteSize) / 2);
        int spriteY = (int) Math.round(y + (height - spriteSize) / 2);
        g.drawImage(SpriteFactory.BALL, spriteX, spriteY, spriteSize, spriteSize, null);
    }
}