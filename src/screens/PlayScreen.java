package screens;

import audio.AudioManager;
import audio.SoundId;
import core.*;
import entities.*;
import effects.Effect;
import effects.buffs.WidePaddleEffect;
import effects.debuffs.ShrinkPaddleEffect;
import levels.Level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayScreen implements GameState {
    private final GameContext cntx;
    private final List<Brick> bricks = new ArrayList<>();
    private final List<Pickup> pickups = new ArrayList<>();
    private final List<Effect> activeEffects = new ArrayList<>();
    private final List<Laser> lasers = new ArrayList<>();
    private double laserSpawnTimer = 3.0;
    private final List<VerticalLaser> vLasers = new ArrayList<>();
    private double vLaserSpawnTimer = 5.0;
    private final Random random = new Random();
    private Level level;
    private Paddle paddle;
    private Ball ball;
    private int lives = 3;
    private int levelNumber = 1;
    private boolean resumingFromPause;

    public PlayScreen(GameContext cntx, int startingLevel) {
        this.cntx = cntx;
        this.levelNumber = startingLevel;
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
        pickups.clear();
        activeEffects.clear();
        lasers.clear();
        laserSpawnTimer = 3.0;
        vLasers.clear();
        vLaserSpawnTimer = 5.0;
    }

    @Override
    public void exit() { }

    @Override
    public void update(double dt) {
        var in = cntx.input;
        if (in.wasPressed(KeyEvent.VK_P) || in.wasPressed(KeyEvent.VK_ESCAPE)) {
            cntx.changeState(new PauseScreen(cntx, this));
            return;
        }
        paddle.setMovingLeft(in.isDown(KeyEvent.VK_LEFT));
        paddle.setMovingRight(in.isDown(KeyEvent.VK_RIGHT));
        paddle.setMovingUp(in.isDown(KeyEvent.VK_UP));
        paddle.setMovingDown(in.isDown(KeyEvent.VK_DOWN));

        paddle.update(dt);


        double maxMove = Math.max(Math.abs(ball.getDx()), Math.abs(ball.getDy())) * dt;
        int steps = Math.max(1, (int) Math.ceil(maxMove / 4.0));
        for(int i = 0; i < steps; i++) {
            ball.update( dt / steps);
            Brick broken = CollisionSystem.resolve(ball, paddle, bricks);
            
            if(broken != null) {
                Effect drop = null;
                
                if (levelNumber == 2) {
                    // Level 2: 20% chance for a buff
                    if (random.nextDouble() < 0.20) {
                        drop = new WidePaddleEffect(70); 
                    }
                } else if (levelNumber >= 3) {
                    // Level 3+: 30% total drop rate (15% buff, 15% debuff)
                    double roll = random.nextDouble();
                    if (roll < 0.15) {
                        drop = new WidePaddleEffect(70); 
                    } else if (roll < 0.30) {
                        drop = new ShrinkPaddleEffect(70);
                    }
                }

                if (drop != null) {
                    // Center the pickup horizontally on the broken brick
                    double px = broken.getX() + (broken.getWidth() - 24) / 2.0;
                    pickups.add(new Pickup(px, broken.getY(), drop));
                }
            }
        }
        bricks.removeIf(b -> !b.isAlive());

        // Update and collide pickups
        for (int i = pickups.size() - 1; i >= 0; i--) {
            Pickup p = pickups.get(i);
            p.update(dt);
            
            // Check collision with paddle
            if (p.getBounds().intersects(paddle.getBounds())) {
                Effect newEffect = p.getEffect();
                boolean alreadyActive = false;
                
                // Check if we already have this effect running
                for (Effect active : activeEffects) {
                    if (active.getName().equals(newEffect.getName())) {
                        active.refresh(); // Just reset the timer
                        alreadyActive = true;
                        break;
                    }
                }
                
                // If it's not already active, apply it and add to list
                if (!alreadyActive) {
                    newEffect.apply(paddle);
                    activeEffects.add(newEffect);
                }
                
                AudioManager.play(SoundId.PICKUP);
                pickups.remove(i);
            }
            // Remove if off-screen
            else if (p.isOffScreen()) {
                pickups.remove(i);
            }
        }

        // Update active effects and remove expired ones
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            Effect e = activeEffects.get(i);
            e.update(dt);
            if (e.isExpired()) {
                e.remove(paddle);
                activeEffects.remove(i);
            }
        }

        // --- LASER SPAWNING LOGIC (Level 2+) ---
        if (levelNumber >= 2) {
            laserSpawnTimer -= dt;
            if (laserSpawnTimer <= 0) {
                // Pick a random Y height within the paddle's movable area
                // We subtract 20 so the laser doesn't clip outside the bounds
                double spawnY = GameField.PADDLE_MIN_Y + random.nextDouble() * (GameField.PADDLE_MAX_Y - GameField.PADDLE_MIN_Y - 20);
                
                lasers.add(new Laser(spawnY));
                laserSpawnTimer = 2.0 + random.nextDouble() * 2.0; // Spawns every 2 to 4 seconds
            }
        }

        // --- VERTICAL LASER SPAWNING LOGIC (Level 3+) ---
        if (levelNumber >= 3) {
            vLaserSpawnTimer -= dt;
            if (vLaserSpawnTimer <= 0) {
                // Pick a random X position within the playable walls
                // We subtract 20 so it doesn't clip outside the right wall
                double spawnX = GameField.LEFT + random.nextDouble() * (GameField.RIGHT - GameField.LEFT - 20);
                
                vLasers.add(new VerticalLaser(spawnX));
                vLaserSpawnTimer = 3.0 + random.nextDouble() * 2.0; // Spawns every 3 to 5 seconds
            }
        }

        // --- LASER UPDATE & COLLISION ---
        for (int i = lasers.size() - 1; i >= 0; i--) {
            Laser l = lasers.get(i);
            l.update(dt);
            
            if (l.isFinished()) {
                lasers.remove(i);
            } else if (l.isActive() && l.getBounds().intersects(paddle.getBounds())) {
                // If the paddle touches the active white laser, remove the laser and lose a life
                lasers.remove(i);
                loseLife();
                break; 
            }
        }

        // --- VERTICAL LASER UPDATE & COLLISION ---
        for (int i = vLasers.size() - 1; i >= 0; i--) {
            VerticalLaser vl = vLasers.get(i);
            vl.update(dt);
            
            if (vl.isFinished()) {
                vLasers.remove(i);
            } else if (vl.isActive() && vl.getBounds().intersects(paddle.getBounds())) {
                vLasers.remove(i);
                loseLife();
                break; 
            }
        }

        // --- GAME OVER / LIFE LOSS LOGIC ---
        if(ball.getY() > GameField.HEIGHT) {
            loseLife();
        }

        // --- LEVEL UP LOGIC ---
        if (bricks.isEmpty()) {
            levelNumber++;
            if (levelNumber > 3) {
                levelNumber = 1; // Loop back to level 1 if they beat level 3
            }
            lives = 3; // Give lives back on level up
            enter();   // Load the next level
        }
    }

    void resume() {
        resumingFromPause = true;
        cntx.changeState(this);
    }

    private void loseLife() {
        AudioManager.play(SoundId.LIFE_LOST);
        lives--;
        
        if (lives <= 0) {
            // True Game Over: Reset to Level 1 and reload everything
            levelNumber = 1;
            lives = 3;
            enter(); 
        } else {
            // Just lost a life, but still have lives left: Keep bricks!
            ball = new Ball();
            paddle = new Paddle(); 
            pickups.clear();
            lasers.clear(); // Clear lasers so you don't instantly die again
            vLasers.clear();
            activeEffects.clear();
        }
    }

    @Override
    public void render(Graphics2D g) {
        GameField.draw(g);
        level.WallAccents(g, GameField.WIDTH, GameField.WALL_SIDE);

        g.setColor(GameField.WALL);
        //here you should add like level no, no. of lives, score, buffs, debuffs etc
        for(Brick b : bricks)
            b.draw(g);
        
        for(Pickup p : pickups)
            p.draw(g);
            
        for(Laser l : lasers)
            l.draw(g);
            
        for(VerticalLaser vl : vLasers)
            vl.draw(g);
            
        paddle.draw(g);
        ball.draw(g);

        drawHUD(g);
    }

    private void drawHUD(Graphics2D g) {
        int hudX = GameField.WIDTH + 20; // Just outside the right wall
        int hudY = 50;
        int barWidth = 150;
        int barHeight = 20;

        g.setFont(new Font("Arial", Font.BOLD, 14));

        // Draw Level and Lives info
        g.setColor(Color.WHITE);
        g.drawString("LEVEL: " + levelNumber, hudX, hudY);
        g.drawString("LIVES: " + lives, hudX, hudY + 25);
        
        // Shift the effect HUD down so it doesn't overlap the level/lives
        hudY += 70;

        for (Effect effect : activeEffects) {
            // Determine color based on effect type
            boolean isDebuff = effect.getName().toLowerCase().contains("shrink");
            Color themeColor = isDebuff ? Color.RED : Color.GREEN;

            // 1. Draw Background Box
            g.setColor(new Color(40, 40, 40));
            g.fillRect(hudX - 5, hudY - 15, barWidth + 10, 45);

            // 2. Draw Text (e.g., "Wide Paddle: 8.5s")
            g.setColor(Color.WHITE);
            String text = String.format("%s: %.1fs", effect.getName(), Math.max(0, effect.getTimeRemaining()));
            g.drawString(text, hudX, hudY);

            // 3. Draw Progress Bar Background
            g.setColor(Color.DARK_GRAY);
            g.fillRect(hudX, hudY + 5, barWidth, barHeight);

            // 4. Draw Progress Bar Fill
            double percent = effect.getTimeRemaining() / effect.getDuration();
            percent = Math.clamp(percent, 0, 1); // Ensure it stays between 0 and 1
            g.setColor(themeColor);
            g.fillRect(hudX, hudY + 5, (int)(barWidth * percent), barHeight);

            // Move down for the next effect
            hudY += 60;
        }
    }
}
