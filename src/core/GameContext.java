package core;

public class GameContext {
    public final GameManager manager;
    public final InputHandler input;

    public GameContext(GameManager manager, InputHandler input) {
        this.manager = manager;
        this.input = input;
    }
    public void changeState(GameState next) {
        manager.changeState(next);
    }
}
