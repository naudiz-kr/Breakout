package levels;

import entities.Brick;
import java.util.List;

public class Level {
    private LevelConfig config;

    public Level() {
        // Instantiates the default Level 1 config
        this.config = new LevelConfig();
    }

    /**
     * Populates the provided list with Brick entities based on the config.
     * Clears the list first to ensure a fresh start.
     */
    public void loadBricks(List<Brick> bricks, int screenWidth) {
        bricks.clear();
        
        int startX = (screenWidth - (config.cols * config.brickWidth + (config.cols - 1) * config.brickGap)) / 2;
        int startY = config.startY;

        for (int r = 0; r < config.rows; r++) {
            for (int c = 0; c < config.cols; c++) {
                double x = startX + c * (config.brickWidth + config.brickGap);
                double y = startY + r * (config.brickHeight + config.brickGap);
                
                bricks.add(new Brick(x, y, config.brickWidth, config.brickHeight));
            }
        }
    }
}