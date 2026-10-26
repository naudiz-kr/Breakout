package persistence;

/**
 * Interface defining the operations for saving and retrieving high scores.
 */
public interface ScoreRepository {
    
    /**
     * Saves a new score to the database.
     */
    void saveScore(ScoreEntry entry);

    /**
     * Retrieves the single highest score ever achieved.
     * Returns 0 if there are no scores in the database.
     */
    int getHighScore();
}