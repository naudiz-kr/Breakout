package levels;

public class LevelConfig {
    public int rows;
    public int cols;
    public int brickWidth;
    public int brickHeight;
    public int brickGap;
    public int startY;

    // We hardcode the default Level 1 values here for now.
    // Later, we can pass an ID to this constructor to load different layouts.
    public LevelConfig() {
        this.rows = 5;
        this.cols = 10;
        this.brickWidth = 70;
        this.brickHeight = 25;
        this.brickGap = 5;
        this.startY = 60;
    }
}