package gateway;

import model.Log;
import model.User;

import javax.naming.SizeLimitExceededException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;

public class UserGateway {
    static private final Logger logger = Log.getInstance().getLogger();
    private static UserGateway instance = null;

    public static UserGateway getInstance() {
        logger.info("Retrieving UserGateway instance...");
        if (instance == null) {
            logger.info("UserGateway first initialization...");
            instance = new UserGateway();
        }
        logger.info("UserGateway instance retrieved");
        return instance;
    }

    public void register(String username, String password, String name, String surname, String role) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Registering new " + role + " (" + username + ")...");
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES(default, ?, ?, ?, crypt(?, gen_salt('bf')),?)")) {
            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.setString(5, role);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Registered new " + role + " (" + username + ")");
    }

    public User login(String email, String password) throws SQLException, SizeLimitExceededException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Logging " + email + " in...");
        ResultSet resultSet = null;
        User user;

        try (PreparedStatement statement = connection.prepareStatement("SELECT id, name, surname, email, role FROM users WHERE email = ? AND password = crypt(?, password)")) {
            statement.setString(1, email);
            statement.setString(2, password);
            logger.fine("QUERY HIDDEN DUE TO PRIVACY REASONS");
            resultSet = statement.executeQuery();
            if (resultSet.next())
                user = new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
            else throw new IllegalArgumentException("Wrong credentials");
            if (resultSet.next())
                throw new SizeLimitExceededException("More than a user is defined with the same credentials");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        return user;
    }

    public void removeUser(String email) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Removing user with email " + email + "...");

        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE email= ?")) {
            statement.setString(1, email);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Removed user with email " + email);
    }

    public int getMuseumOwned(int userId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Getting museum owned by user " + userId);
        int museumId;
        ResultSet resultSet = null;

        try (PreparedStatement statement = connection.prepareStatement("SELECT fk_museum_id FROM user_museum WHERE fk_user_id = ?")) {
            statement.setInt(1, userId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                museumId = resultSet.getInt(1);
            } else throw new IllegalArgumentException("Provided id doesn't match any user in the database");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("User " + userId + " own museum " + museumId);
        return museumId;
    }

    public void access(int museumId, int userId, Date date) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Registering access to museum with id " + museumId + "...");
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO accesses (access_id, fk_user_id, fk_museum_id, access_date) " +
                        "VALUES(default, ?, ?, ?)")) {
            statement.setInt(1, userId);
            statement.setInt(2, museumId);
            statement.setDate(3, sqlDate);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Access registered");
    }

    public int getDailyAccesses(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        int accesses = 0;
        ResultSet resultSet = null;

        logger.info("Checking daily accesses for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM accesses WHERE fk_museum_id = ? AND access_date = current_date")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                accesses = resultSet.getInt(1);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Daily accesses are " + accesses);
        return accesses;
    }

    public int getWeeklyAccesses(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        int accesses = 0;
        ResultSet resultSet = null;

        logger.info("Checking weekly accesses for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM accesses WHERE fk_museum_id = ? AND access_date BETWEEN current_date - INTERVAL '6 days' and current_date")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                accesses = resultSet.getInt(1);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Weekly accesses are " + accesses);
        return accesses;
    }

    public int getAllTimeAccesses(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        int accesses = 0;
        ResultSet resultSet = null;

        logger.info("Checking all time accesses for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM accesses WHERE fk_museum_id = ?")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                accesses = resultSet.getInt(1);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("All time accesses are " + accesses);
        return accesses;
    }
}
