package gateway;

import model.Event;
import model.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class EventGateway {
    static private final Logger logger = Log.getInstance().getLogger();
    private static EventGateway instance = null;

    public static EventGateway getInstance() {
        logger.info("Retrieving EventGateway instance...");
        if (instance == null) {
            logger.info("EventGateway first initialization...");
            instance = new EventGateway();
        }
        logger.info("EventGateway instance retrieved");
        return instance;
    }

    public final ArrayList<Event> getEvents(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        ArrayList<Event> events = new ArrayList<>();
        ResultSet resultSet = null;

        logger.info("Retrieving events for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT event_description FROM events WHERE fk_museum_id = ? AND current_date <= date_end ORDER BY event_id DESC")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                events.add(new Event(resultSet.getString(1)));
            }
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Events retrieved");
        return events;
    }

    public void createEvent(int museumId, String description, Date start, Date end, int typology, int discount) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Creating event for museum with id " + museumId + "...");
        java.sql.Date sqlStart = new java.sql.Date(start.getTime());
        java.sql.Date sqlEnd = new java.sql.Date(end.getTime());
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO events (event_id, fk_museum_id, event_description, date_start, date_end, typology, discount) " +
                        "VALUES(default, ?, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, museumId);
            statement.setString(2, description);
            statement.setDate(3, sqlStart);
            statement.setDate(4, sqlEnd);
            statement.setInt(5, typology);
            statement.setInt(6, discount);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Event created");
    }

    public boolean isClosed(int museumId, Date date) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        boolean closed = true;
        ResultSet resultSet = null;
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        logger.info("Checking if museum with id " + museumId + " is closed...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT exists(SELECT TRUE FROM events WHERE fk_museum_id = ? AND typology = 2 AND ? between date_start and date_end)")) {
            statement.setInt(1, museumId);
            statement.setDate(2, sqlDate);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                closed = resultSet.getBoolean(1);
            }
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        if (closed)
            logger.info("Museum is closed");
        else logger.info("Museum is open");
        return closed;
    }

    public final float getTicketPrice(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        float price = 10;
        ResultSet resultSet = null;

        logger.info("Checking ticket price for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT discount FROM events WHERE fk_museum_id = ? AND typology = 1 AND current_date between date_start and date_end ORDER BY event_id DESC")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                price = price * (1 - (resultSet.getFloat(1) / 100));
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Ticket price is " + price);
        return price;
    }
}
