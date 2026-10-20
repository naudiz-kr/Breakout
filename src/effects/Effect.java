package effects;

import entities.Paddle;


//Interface for all buffs and debuffs.
//Implementations will modify the paddle (or ball) and then revert the changes.
public interface Effect {
    //Called when the paddle collects the pickup.
    void apply(Paddle paddle);
    /**
     * Called when the effect duration expires or the level ends.
     */
    void remove(Paddle paddle);

    /**
     * Called when the player catches the same effect while it's already active.
     * Should reset the internal timer without applying the effect again.
     */
    void refresh();

    /**
     * Called every frame to update the effect's internal timer.
     */
    void update(double dt);

    /**
     * Returns true if the effect's duration is over.
     */
    boolean isExpired();
    
    /**
     * Returns a string name for the effect (useful for UI/HUD later).
     */
    String getName();

    /**
     * Returns the time remaining in seconds (for UI).
     */
    double getTimeRemaining();

    /**
     * Returns the total duration of the effect in seconds (for UI bars).
     */
    double getDuration();
}