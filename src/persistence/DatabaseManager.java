package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the SQLite database connection and schema initialization.
 */
public class DatabaseManager {
    // The database file will be created in the root project folder
    private static final String URL = "jdbc:sqlite:highscores.db";

    /**
     * Returns a connection to the SQLite database.
     * Ensures the required table exists before returning.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        createTableIfNotExists(conn);
        return conn;
    }

    /**
     * Creates the highscores table if it doesn't already exist.
     */
    private static void createTableIfNotExists(Connection conn) {
        // SQL statement to create the table
        String sql = "CREATE TABLE IF NOT EXISTS highscores (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "score INTEGER NOT NULL," +
                     "date TEXT NOT NULL" +
                     ");";
                     
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating database table: " + e.getMessage());
        }
    }
}