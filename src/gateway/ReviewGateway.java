package gateway;

import model.Log;
import model.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ReviewGateway {
    static private final Logger logger = Log.getInstance().getLogger();
    private static ReviewGateway instance = null;


    public static ReviewGateway getInstance() {
        logger.info("Retrieving ReviewGateway instance...");
        if (instance == null) {
            logger.info("ReviewGateway first initialization...");
            instance = new ReviewGateway();
        }
        logger.info("ReviewGateway instance retrieved");
        return instance;
    }

    public void writeReview(int museumId, int userId, String reviewText, int score) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Storing review...");
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO reviews (review_id, fk_museum_id, fk_user_id, review_text, review_score) " +
                        "VALUES(default, ?, ?, ?, ?)")) {
            statement.setInt(1, museumId);
            statement.setInt(2, userId);
            statement.setString(3, reviewText);
            statement.setInt(4, score);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Review stored");
    }

    public int getReviewsNumber(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        int reviews = 0;
        ResultSet resultSet = null;

        logger.info("Checking reviews number for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM reviews WHERE fk_museum_id = ?")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                reviews = resultSet.getInt(1);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Reviews number is " + reviews);
        return reviews;
    }

    public float getAverageScore(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        float score = 0;
        ResultSet resultSet = null;

        logger.info("Checking average review score for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT avg(review_score) FROM reviews WHERE fk_museum_id = ?")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                score = resultSet.getFloat(1);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Average score is " + score);
        return score;
    }

    public final ArrayList<Review> getReviews(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        ArrayList<Review> reviews = new ArrayList<>();
        ResultSet resultSet = null;

        logger.info("Retrieving reviews of museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM reviews WHERE fk_museum_id = ?")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                reviews.add(new Review(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getInt(3), resultSet.getString(4), resultSet.getInt(5)));
            }
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Reviews retrieved");
        return reviews;
    }
}
