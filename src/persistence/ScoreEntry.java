package persistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple data class representing a single high score entry.
 */
public class ScoreEntry {
    private final int score;
    private final String date;

    // Constructor for creating a NEW score during gameplay
    public ScoreEntry(int score) {
        this.score = score;
        // Store the current date and time as a formatted string
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Constructor for loading an EXISTING score from the database
    public ScoreEntry(int score, String date) {
        this.score = score;
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }
}