package score;

import java.util.List;

public interface ScoreRepository {
    /** Saves a level score for a player; keeps the higher one if it already exists. */
    void saveLevelScore(String name, int level, int score);
    boolean nameExists(String name);
    int getBestForLevel(int level);
    List<PlayerRow> getTopPlayers(int limit);

    /** levels[0..2] = score per level (0 = not played). */
    record PlayerRow(String name, int[] levels, int total) { }
}