package levels;

import entities.Brick;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class Level {
    private static final Color[] PAIR_COLORS = {
        new Color(168, 14, 14),           //red
        new Color(227, 98, 16),         //orange
        new Color(24, 132, 40),         //green
        new Color(238, 233, 9),        //yellow
    };
    private static final int[] PAIR_POINTS = {7, 5, 3, 1}; //points per brick
    private final LevelConfig config;

    public Level(int levelNumber) {
        this.config = new LevelConfig(levelNumber);
    }

    /**
     * Populates the provided list with Brick entities based on the config.
     * Clears the list first to ensure a fresh start.
     */
    public void loadBricks(List<Brick> bricks, int screenWidth) {
        bricks.clear();
        
        int startX = (screenWidth - (config.cols * config.brickWidth + (config.cols - 1) * config.brickGap)) / 2;

        for (int r = 0; r < config.rows; r++) {
            // Modulo ensures we don't crash if the level has more than 8 rows
            int pair = (r / 2) % PAIR_COLORS.length; 
            for (int c = 0; c < config.cols; c++) {
                double x = startX + c * (config.brickWidth + config.brickGap);
                double y = config.startY + r * (config.brickHeight + config.brickGap);
                
                bricks.add(new Brick(x, y, config.brickWidth, config.brickHeight, PAIR_COLORS[pair], PAIR_POINTS[pair]));
            }
        }
    }
    /* OPTIONAL !!!! Coloured wall segments besides each pair of brick colors. Overcolours the gray */
    public void WallAccents(Graphics2D g, int screenWidth, int wallThickness) {
        int pitch = config.brickHeight + config.brickGap;
        for (int pair = 0; pair < config.rows / 2; pair++) {
            int y = config.startY + pair * 2 * pitch;
            int h = 2 * pitch - config.brickGap;
            g.setColor(PAIR_COLORS[pair]);
            g.fillRect(0, y, wallThickness, h);
            g.fillRect(screenWidth - wallThickness, y, wallThickness, h);
        }
    }
}