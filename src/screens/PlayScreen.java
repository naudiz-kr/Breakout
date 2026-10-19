package screens;

import core.*;
import entities.*;
import levels.Level;

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
                         //Need to see if 3 lives and also add score

    public PlayScreen(GameContext cntx) {
        this.cntx = cntx;
    }

    @Override
    public void enter() {
        level = new Level();
        paddle = new Paddle();
        ball = new Ball();
        level.loadBricks(bricks, GameField.WIDTH);
    }

    @Override
    public void exit() { }

    @Override
    public void update(double dt) {
        var in = cntx.input;
        paddle.setMovingLeft(in.isDown(KeyEvent.VK_LEFT));
        paddle.setMovingRight(in.isDown(KeyEvent.VK_RIGHT));
        paddle.setMovingUp(in.isDown(KeyEvent.VK_UP));
        paddle.setMovingDown(in.isDown(KeyEvent.VK_DOWN));

        paddle.update();
        ball.update();

       Brick broken = CollisionSystem.resolve(ball, paddle, bricks);
        /*if(broken != null)
            //score += broken.getPoints(); */
        bricks.removeIf(b -> !b.isAlive());

        if(ball.getY() > GameField.HEIGHT)
            ball = new Ball();
    }

    @Override
    public void render(Graphics2D g) {
        GameField.draw(g);
        level.WallAccents(g, GameField.WIDTH, GameField.WALL_SIDE);

        g.setColor(GameField.WALL);
        //here you should add like level no, no. of lives, score, buffs, debuffs etc
        for(Brick b : bricks)
            b.draw(g);
        paddle.draw(g);
        ball.draw(g);
    }
}
