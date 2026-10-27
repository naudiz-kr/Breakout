package screens;

import audio.AudioManager;
import audio.SoundId;
import core.*;
import entities.*;
import levels.Level;
import score.SqliteScoreRepository;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PlayScreen implements GameState {
    private final GameContext cntx;
    private final List<Brick> bricks = new ArrayList<>();
    private Level level;
    private Paddle paddle;
    private Ball ball;
    private int lives = 3;
    private int levelNumber;
    private int score = 0;
    private final int highScore;
    private boolean resumingFromPause;
    private boolean finished;

    public PlayScreen(GameContext cntx, int startingLevel) {
        this.cntx = cntx;
        this.levelNumber = startingLevel;
        this.highScore = new SqliteScoreRepository().getBestForLevel(startingLevel);
    }

    @Override
    public void enter() {
        if (resumingFromPause) {
            resumingFromPause = false;
            return;
        }
        level = new Level(levelNumber);
        paddle = new Paddle();
        ball = new Ball();
        level.loadBricks(bricks, GameField.WIDTH);
        CollisionSystem.clearPickups();
        CollisionSystem.resetLasers();
    }

    @Override
    public void exit() { }

    @Override
    public void update(double dt) {
        if (finished || handlePause()) return;
        updatePaddle(dt);
        stepBall(dt);
        bricks.removeIf(b -> !b.isAlive());
        CollisionSystem.updatePickups(dt, paddle);
        if (CollisionSystem.updateLasers(dt, levelNumber, paddle)) loseLife();
        if (!finished && ball.getY() > GameField.HEIGHT) loseLife();
        if (!finished && bricks.isEmpty()) endLevel(true);
    }

    private boolean handlePause() {
        var in = cntx.input;
        if (in.wasPressed(KeyEvent.VK_P) || in.wasPressed(KeyEvent.VK_ESCAPE)) {
            cntx.changeState(new PauseScreen(cntx, this));
            return true;
        }
        return false;
    }

    private void updatePaddle(double dt) {
        var in = cntx.input;
        paddle.setMovingLeft(in.isDown(KeyEvent.VK_LEFT));
        paddle.setMovingRight(in.isDown(KeyEvent.VK_RIGHT));
        paddle.setMovingUp(in.isDown(KeyEvent.VK_UP));
        paddle.setMovingDown(in.isDown(KeyEvent.VK_DOWN));
        paddle.update(dt);
    }

    private void stepBall(double dt) {
        double maxMove = Math.max(Math.abs(ball.getDx()), Math.abs(ball.getDy())) * dt;
        int steps = Math.max(1, (int) Math.ceil(maxMove / 4.0));
        for (int i = 0; i < steps; i++) {
            ball.update(dt / steps);
            Brick broken = CollisionSystem.resolve(ball, paddle, bricks);
            if (broken != null) {
                score += broken.getPoints();
                CollisionSystem.maybeDrop(broken, levelNumber);
            }
        }
    }

    private void loseLife() {
        AudioManager.play(SoundId.LIFE_LOST);
        lives--;
        if (lives <= 0) {
            endLevel(false);
        } else {
            ball = new Ball();
            paddle = new Paddle();
            CollisionSystem.clearPickups();
            CollisionSystem.clearLasers();
        }
    }

    private void endLevel(boolean won) {
        finished = true;
        cntx.changeState(new ResultScreen(cntx, levelNumber, score, won));
    }

    int getScore() { return score; }
    int getLevelNumber() { return levelNumber; }

    void resume() {
        resumingFromPause = true;
        cntx.changeState(this);
    }

    @Override
    public void render(Graphics2D g) {
        GameField.draw(g);
        level.WallAccents(g, GameField.WIDTH, GameField.WALL_SIDE);
        for (Brick b : bricks) b.draw(g);
        CollisionSystem.drawPickups(g);
        CollisionSystem.drawLasers(g);
        paddle.draw(g);
        ball.draw(g);
        Hud.draw(g, levelNumber, lives, score, highScore, CollisionSystem.getActiveEffects());
    }
}