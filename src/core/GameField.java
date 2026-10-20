package core;

import java.awt.Color;
import java.awt.Graphics2D;
/* this entire section creates the place where the game takes place*/
public final class GameField {
    public static final int WIDTH = 740, HEIGHT = 720;
    public static final int WALL_SIDE = 12, WALL_TOP = 24;
    public static final int LEFT = WALL_SIDE;
    public static final int RIGHT = WIDTH - WALL_SIDE;
    public static final int TOP = WALL_TOP;
    public static final int PADDLE_Y = 660;
    

    public static final int PADDLE_MIN_Y = 396;
    public static final int PADDLE_MAX_Y = 700;

    public static final double BASE_SPEED = 420.0; //ie 4.0

    public static final Color WALL = new Color(64, 64, 64);
    public static final Color PADDLE_BLUE = new Color(24, 120, 200);

    private GameField() { }

    public static void draw(Graphics2D g) {
        g.setColor(WALL);
        g.fillRect(0, 0, WIDTH, WALL_TOP);
        g.fillRect(0, 0, WALL_SIDE, HEIGHT);
        g.fillRect(RIGHT, 0, WALL_SIDE, HEIGHT);

        g.setColor(PADDLE_BLUE);
        int h = PADDLE_MAX_Y - PADDLE_MIN_Y + 12;
        g.fillRect(0, PADDLE_MIN_Y, WALL_SIDE, h);
        g.fillRect(RIGHT, PADDLE_MIN_Y, WALL_SIDE, h);
    }
}
