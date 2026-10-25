package core;

import audio.AudioManager;
import audio.SoundId;
import entities.*;
import java.util.List;

public final class CollisionSystem {
    private static final double MAX_BOUNCE_ANGLE = Math.toRadians(60);

    private CollisionSystem() {}

    public static Brick resolve(Ball ball, Paddle paddle, List<Brick> bricks) {
        resolvePaddle(ball, paddle);
        return resolveBricks(ball, bricks);     /* This returns either the brick broken in a frame or null*/
    }
    private static void resolvePaddle(Ball ball, Paddle paddle) {
        double ox = overlapX(ball, paddle);
        double oy = overlapY(ball, paddle);
        if (ox <= 0 || oy <= 0) return;
        AudioManager.play(SoundId.PADDLE_BOUNCE);

        boolean hitFromTop = oy <= ox && centerY(ball) < centerY(paddle);

        if (hitFromTop) {
            ball.setY(paddle.getY() - ball.getHeight());

            double speed = Math.hypot(ball.getDx(), ball.getDy());
            double rel = (centerX(ball) - centerX(paddle)) / (paddle.getWidth() / 2);
            rel = Math.clamp(rel, -1, 1);
            double angle = rel * MAX_BOUNCE_ANGLE;

            ball.setVelocity(speed * Math.sin(angle), -speed * Math.cos(angle));
        } else {
            reflect(ball, paddle, ox, oy);
        }
    }
    private static Brick resolveBricks(Ball ball, List <Brick> bricks) {
        Brick best = null;
        double bestArea = 0;

        for(Brick b : bricks) {
            if(!b.isAlive())
                continue;
            double ox = overlapX(ball, b);
            double oy = overlapY(ball, b);
            if(ox <= 0 || oy <= 0)
                continue;
            if(ox * oy > bestArea) {
                bestArea = ox * oy;
                best = b;
            }
        }
        if(best == null)
            return null;
        reflect(ball, best, overlapX(ball, best), overlapY(ball, best));
        best.hit();
        AudioManager.play(SoundId.BRICK_BREAK);
        return best;
    }
    private static void reflect(Ball ball, Entity other, double ox, double oy) {
        if(ox < oy) {
            boolean ballIsLeft = centerX(ball) < centerX(other);
            ball.setX(ballIsLeft ? other.getX() - ball.getWidth() : other.getX() + other.getWidth());
            double dx = Math.abs(ball.getDx());
            ball.setVelocity(ballIsLeft ? -dx : dx, ball.getDy());
        } else {
            boolean ballIsAbove = centerY(ball) < centerY(other);
            ball.setY(ballIsAbove ? other.getY() - ball.getHeight() : other.getY() + other.getHeight());
            double dy = Math.abs(ball.getDy());
            ball.setVelocity(ball.getDx(), ballIsAbove ? -dy : dy);
        }
    }
    private static double overlapX(Entity a, Entity b) {
        return Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth()) - Math.max(a.getX(), b.getX());
    }
    private static double overlapY(Entity a, Entity b) {
        return Math.min(a.getY() + a.getHeight(), b.getY() + b.getHeight()) - Math.max(a.getY(), b.getY());
    }
    private static double centerX(Entity e) {
        return e.getX() + e.getWidth() / 2;
    }
    private static double centerY(Entity e) {
        return e.getY() + e.getHeight() / 2;
    }
}
