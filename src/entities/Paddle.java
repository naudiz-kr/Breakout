package entities;

import java.awt.Color;
import java.awt.Graphics2D;

public class Paddle extends Entity {
    private static final double SPEED = 7.0;
    private final int screenWidth;
    private final int screenHeight;
    private boolean movingLeft, movingRight;
    private boolean movingUp, movingDown;

    public Paddle(int screenWidth, int screenHeight) {
        super(screenWidth / 2.0 - 50, screenHeight - 60, 100, 15);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setMovingLeft(boolean b)  { movingLeft = b; }
    public void setMovingRight(boolean b) { movingRight = b; }
    public void setMovingUp(boolean b)    { movingUp = b; }
    public void setMovingDown(boolean b)  { movingDown = b; }

    @Override
    public void update() {
        // Horizontal movement
        if (movingLeft)  x -= SPEED;
        if (movingRight) x += SPEED;
        
        // Vertical movement
        if (movingUp)    y -= SPEED;
        if (movingDown)  y += SPEED;

        // Horizontal bounds
        if (x < 0) x = 0;
        if (x + width > screenWidth) x = screenWidth - width;

        // Vertical bounds (Bottom third of the screen)
        // NOTE FOR KARTHIK: This clamps the paddle to the bottom 33% of the screen 
        // i want it to be able to move in 2D
        double minY = screenHeight * (2.0 / 3.0); // Top of the bottom third
        double maxY = screenHeight - height;      // Bottom of the screen
        
        if (y < minY) y = minY;
        if (y > maxY) y = maxY;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRoundRect((int) x, (int) y, (int) width, (int) height, 8, 8);
    }
}