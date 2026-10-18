package core;

public class GameLoop {
    private static final double STEP = 1.0 / 60.0;
    private static final double MAX_FRAME = 0.25;

    private final GameManager manager;
    private final javax.swing.Timer swingTimer;
    private long last;
    private double accumulator;

    public GameLoop(GameManager manager) {
        this.manager = manager;
        this.swingTimer = new javax.swing.Timer(4, e -> tick());
    }

    public void start() {
        last = System.nanoTime();
        accumulator = 0;
        swingTimer.start();
    }

    public void stop() {
        swingTimer.stop();
    }

    private void tick() {
        long now = System.nanoTime();
        accumulator += Math.min((now - last) / 1e9, MAX_FRAME);
        last = now;

        boolean stepped = false;
        while(accumulator >= STEP) {
            manager.update(STEP);
            accumulator -= STEP;
            stepped = true;
        }
        if(stepped)
            manager.repaint();
    }
}
