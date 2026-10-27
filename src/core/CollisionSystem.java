package core;

import audio.AudioManager;
import audio.SoundId;
import entities.*;
import effects.Effect;
import effects.buffs.WidePaddleEffect;
import effects.debuffs.ShrinkPaddleEffect;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class CollisionSystem {
    private static final double MAX_BOUNCE_ANGLE = Math.toRadians(60);
    private static final Random random = new Random();
    private static final List<Laser> lasers = new ArrayList<>();
    private static double hTimer = 3.0;
    private static double vTimer = 5.0;
    private static final List<Pickup> pickups = new ArrayList<>();
    private static final List<Effect> activeEffects = new ArrayList<>();

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
    // ---------------- LASERS ----------------
    public static void resetLasers() {
        clearLasers();
        hTimer = 3.0;
        vTimer = 5.0;
    }

    public static void clearLasers() {
        lasers.clear();
    }

    /** Spawns, updates and collides lasers. Returns true if one hit the paddle. */
    public static boolean updateLasers(double dt, int levelNumber, Paddle paddle) {
        if (levelNumber >= 2) {
            hTimer -= dt;
            if (hTimer <= 0) {
                double y = GameField.PADDLE_MIN_Y
                        + random.nextDouble() * (GameField.PADDLE_MAX_Y - GameField.PADDLE_MIN_Y - 20);
                lasers.add(Laser.horizontal(y));
                hTimer = 2.0 + random.nextDouble() * 2.0;
            }
        }
        if (levelNumber >= 3) {
            vTimer -= dt;
            if (vTimer <= 0) {
                double x = GameField.LEFT
                        + random.nextDouble() * (GameField.RIGHT - GameField.LEFT - 20);
                lasers.add(Laser.vertical(x));
                vTimer = 3.0 + random.nextDouble() * 2.0;
            }
        }
        for (int i = lasers.size() - 1; i >= 0; i--) {
            Laser l = lasers.get(i);
            l.update(dt);
            if (l.isFinished()) {
                lasers.remove(i);
            } else if (l.isActive() && l.getBounds().intersects(paddle.getBounds())) {
                lasers.remove(i);
                return true;
            }
        }
        return false;
    }

    public static void drawLasers(Graphics2D g) {
        for (Laser l : lasers) l.draw(g);
    }

    // ---------------- PICKUPS & EFFECTS ----------------
    public static List<Effect> getActiveEffects() {
        return activeEffects;
    }

    public static void clearPickups() {
        pickups.clear();
        activeEffects.clear();
    }

    /** Rolls for a drop where a brick just broke. */
    public static void maybeDrop(Brick broken, int levelNumber) {
        Effect drop = null;
        double roll = random.nextDouble();
        if (levelNumber == 2) {
            if (roll < 0.20) drop = new WidePaddleEffect(70);
        } else if (levelNumber >= 3) {
            if (roll < 0.15) drop = new WidePaddleEffect(70);
            else if (roll < 0.30) drop = new ShrinkPaddleEffect(70);
        }
        if (drop != null) {
            double px = broken.getX() + (broken.getWidth() - 24) / 2.0;
            pickups.add(new Pickup(px, broken.getY(), drop));
        }
    }

    public static void updatePickups(double dt, Paddle paddle) {
        for (int i = pickups.size() - 1; i >= 0; i--) {
            Pickup p = pickups.get(i);
            p.update(dt);
            if (p.getBounds().intersects(paddle.getBounds())) {
                collect(p.getEffect(), paddle);
                pickups.remove(i);
            } else if (p.isOffScreen()) {
                pickups.remove(i);
            }
        }
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            Effect e = activeEffects.get(i);
            e.update(dt);
            if (e.isExpired()) {
                e.remove(paddle);
                activeEffects.remove(i);
            }
        }
    }

    private static void collect(Effect newEffect, Paddle paddle) {
        AudioManager.play(SoundId.PICKUP);
        for (Effect active : activeEffects) {
            if (active.getName().equals(newEffect.getName())) {
                active.refresh();
                return;
            }
        }
        newEffect.apply(paddle);
        activeEffects.add(newEffect);
    }

    public static void drawPickups(Graphics2D g) {
        for (Pickup p : pickups) p.draw(g);
    }
    private static double centerX(Entity e) {
        return e.getX() + e.getWidth() / 2;
    }
    private static double centerY(Entity e) {
        return e.getY() + e.getHeight() / 2;
    }
}
