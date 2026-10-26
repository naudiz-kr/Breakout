package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteScoreRepository implements ScoreRepository {

    @Override
    public void saveScore(ScoreEntry entry) {
        String sql = "INSERT INTO highscores(score, date) VALUES(?, ?)";
        
        // Try-with-resources automatically closes the connection and statement
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, entry.getScore());
            pstmt.setString(2, entry.getDate());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Failed to save score: " + e.getMessage());
        }
    }

    @Override
    public int getHighScore() {
        String sql = "SELECT MAX(score) AS high_score FROM highscores";
        int highScore = 0;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            if (rs.next()) {
                highScore = rs.getInt("high_score"); // Returns 0 if the table is empty
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to retrieve high score: " + e.getMessage());
        }
        
        return highScore;
    }
}