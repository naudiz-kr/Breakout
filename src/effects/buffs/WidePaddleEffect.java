package effects.buffs;

import effects.Effect;
import entities.Paddle;

public class WidePaddleEffect implements Effect {
    private double widthIncrease;
    private double deltaApplied = 0;
    private boolean isApplied = false;
    private double timeRemaining;
    private final double DURATION = 10.0; // Lasts for 10 seconds

    public WidePaddleEffect(double widthIncrease) {
        this.widthIncrease = widthIncrease;
    }
    @Override
    public void apply(Paddle paddle) {
        if (!isApplied) {
            double before = paddle.getWidth();
            paddle.setWidth(before + widthIncrease);
            this.deltaApplied = paddle.getWidth() - before; // Handles clamping!
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
        return "Wide Paddle";
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