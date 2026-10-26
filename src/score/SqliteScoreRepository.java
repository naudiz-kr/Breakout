package score;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SqliteScoreRepository implements ScoreRepository {

    @Override
    public void saveLevelScore(String name, int level, int score) {
        String sql = "INSERT INTO level_scores(name, level, score, date) VALUES(?,?,?,?) "
                + "ON CONFLICT(name, level) DO UPDATE SET score = MAX(score, excluded.score)";
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, name);
            p.setInt(2, level);
            p.setInt(3, score);
            p.setString(4, now);
            p.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save score: " + e.getMessage());
        }
    }

    @Override
    public boolean nameExists(String name) {
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT 1 FROM level_scores WHERE name = ? LIMIT 1")) {
            p.setString(1, name);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Failed to check name: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getBestForLevel(int level) {
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT MAX(score) FROM level_scores WHERE level = ?")) {
            p.setInt(1, level);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("Failed to get best score: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public List<PlayerRow> getTopPlayers(int limit) {
        Map<String, String> display = new HashMap<>();
        Map<String, int[]> scores = new HashMap<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT name, level, score FROM level_scores");
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                String n = rs.getString(1);
                String key = n.toLowerCase();
                display.putIfAbsent(key, n);
                int lv = rs.getInt(2);
                if (lv >= 1 && lv <= 3) {
                    scores.computeIfAbsent(key, k -> new int[3])[lv - 1] = rs.getInt(3);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load leaderboard: " + e.getMessage());
        }
        List<PlayerRow> rows = new ArrayList<>();
        for (var e : scores.entrySet()) {
            int[] s = e.getValue();
            rows.add(new PlayerRow(display.get(e.getKey()), s, s[0] + s[1] + s[2]));
        }
        rows.sort((a, b) -> b.total() - a.total());
        return rows.size() > limit ? new ArrayList<>(rows.subList(0, limit)) : rows;
    }
}