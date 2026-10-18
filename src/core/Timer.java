package core;
/** Timer to end of level */
public class Timer {
    private final double duration;
    private double remaining;

    public Timer(double durationSec) {
        this.duration = durationSec;
        this.remaining = durationSec;
    }
    public boolean tick(double dt) {
        remaining -= dt;
        if(remaining <= 0) {
            remaining += duration;
            return true;
        }return false;
    }
    public void reset() {
        remaining = duration;
    }
    public double getRemaining() {
        return remaining;
    }
}
