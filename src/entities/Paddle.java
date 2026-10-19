package entities;

import core.GameField;
import java.awt.Graphics2D;

public class Paddle extends Entity {
    private static final double SPEED = 7.0;
    private boolean movingLeft, movingRight, movingUp, movingDown;

    public Paddle() {
        super(GameField.WIDTH / 2.0 - 20, GameField.PADDLE_Y, 56, 12);
    }

    public void setMovingLeft(boolean b)  {
        movingLeft = b;
    }
    public void setMovingRight(boolean b) {
        movingRight = b;
    }
    public void setMovingUp(boolean b) {
        movingUp = b;
    }
    public void setMovingDown(boolean b) {
        movingDown = b;
    }

    @Override
    public void update() {
        // Horizontal movement
        if (movingLeft)  x -= SPEED;
        if (movingRight) x += SPEED;
        
        // Vertical movement
        if (movingUp)    y -= SPEED*0.6;
        if (movingDown)  y += SPEED*0.6;

        // Horizontal bounds
        if (x < GameField.LEFT)
            x = GameField.LEFT;
        if (x + width > GameField.RIGHT)
            x = GameField.RIGHT - width;
        if(y < GameField.PADDLE_MIN_Y)
            y = GameField.PADDLE_MIN_Y;
        if(y > GameField.PADDLE_MAX_Y)
            y = GameField.PADDLE_MAX_Y;


        // Vertical bounds (Bottom third of the screen)
        // NOTE FOR KARTHIK: This clamps the paddle to the bottom 33% of the screen 
        // i want it to be able to move in 2D
         // karthik - that section has been put in gamefield as paddle max-y, paddle min-y. edit as needed.

    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(GameField.PADDLE_BLUE);
        g.fillRect((int) x, (int) y, (int) width, (int) height);
    }
}