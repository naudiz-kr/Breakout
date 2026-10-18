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
        this.rows = 8;
        this.cols = 14;
        this.brickWidth = 36;
        this.brickHeight = 10;
        this.brickGap = 4; //14*36 + 13*4 = 556 as the space between walls.
        this.startY = 140;
    }
}