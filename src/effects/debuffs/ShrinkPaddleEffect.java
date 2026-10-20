package effects.debuffs;

import effects.Effect;
import entities.Paddle;

public class ShrinkPaddleEffect implements Effect {
    private double widthDecrease;
    private double deltaApplied = 0;
    private boolean isApplied = false;
    private double timeRemaining;
    private final double DURATION = 10.0; // Lasts for 10 seconds

    public ShrinkPaddleEffect(double widthDecrease) {
        this.widthDecrease = widthDecrease;
    }

    @Override
    public void apply(Paddle paddle) {
        if (!isApplied) {
            double before = paddle.getWidth();
            paddle.setWidth(before - widthDecrease);
            this.deltaApplied = paddle.getWidth() - before; // Will be negative
            this.timeRemaining = DURATION;
            isApplied = true;
        }
    }

    @Override
    public void remove(Paddle paddle) {
        if (isApplied) {
            paddle.setWidth(paddle.getWidth() - deltaApplied);
            isApplied = false;
        }
    }

    @Override
    public void refresh() {
        if (isApplied) {
            timeRemaining = DURATION;
        }
    }

    @Override
    public void update(double dt) {
        if (isApplied) {
            timeRemaining -= dt;
        }
    }

    @Override
    public boolean isExpired() {
        return isApplied && timeRemaining <= 0;
    }

    @Override
    public String getName() {
        return "Shrink Paddle";
    }

    @Override
    public double getTimeRemaining() {
        return timeRemaining;
    }

    @Override
    public double getDuration() {
        return DURATION;
    }
}