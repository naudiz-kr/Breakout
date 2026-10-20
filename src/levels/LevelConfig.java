package levels;

public class LevelConfig {
    public int rows;
    public int cols;
    public int brickWidth;
    public int brickHeight;
    public int brickGap;
    public int startY;

    public LevelConfig(int levelNumber) {
        // Standard default grid settings (16 columns * 40px = 640px + gaps = ~700px)
        this.cols = 16;
        this.brickWidth = 40;
        this.brickHeight = 15;
        this.brickGap = 4;
        this.startY = 120;

        // Level 1: 3 layers of bricks (Pure skill, no drops)
        if (levelNumber == 1) {
            this.rows = 3; 
        } 
        // Level 2: 5 layers of bricks (Buffs + Enemies)
        else if (levelNumber == 2) {
            this.rows = 5;
        } 
        // Level 3: 8 layers of bricks (Buffs + Debuffs + Enemies)
        else if (levelNumber >= 3) {
            this.rows = 8;
        }
    }
}