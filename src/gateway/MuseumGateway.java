package gateway;

import model.*;
import org.json.JSONArray;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MuseumGateway {
    static private final Logger logger = Log.getInstance().getLogger();
    private static MuseumGateway instance = null;
    private SearchStrategy searchStrategy = new ScoreStrategy();


    public static MuseumGateway getInstance() {
        logger.info("Retrieving MuseumGateway instance...");
        if (instance == null) {
            logger.info("MuseumGateway first initialization...");
            instance = new MuseumGateway();
        }
        logger.info("MuseumGateway instance retrieved");
        return instance;
    }

    public void setStrategy(SearchStrategy searchStrategy) {
        logger.info("Setting new search strategy (" + searchStrategy.getClass().getName() + ")...");
        this.searchStrategy = searchStrategy;
        logger.info(searchStrategy.getClass().getName() + " set");
    }

    public JSONArray searchMuseums(String query, String location) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Searching museums using query \"" + query + "\" and location \"" + location + "\"...");
        String[] keywords = splitQuery(query);
        String select = searchStrategy.buildSelect(keywords, location);
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(select)) {
            resultSet.next();
            return new JSONArray(resultSet.getString(1));
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
    }

    private String[] splitQuery(String query) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Splitting query \"" + query + "\" into keywords...");
        ResultSet resultSet = null;
        String[] keywords = new String[0];

        try (PreparedStatement statement = connection.prepareStatement("SELECT array_to_string(tsvector_to_array(to_tsvector('simple_italian', ?)), '|');")) {
            statement.setString(1, query);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            resultSet.next();
            keywords = resultSet.getString(1).split("\\|");
        } catch (SQLException e) {
            logger.severe(Log.getStringStackTrace(e));
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }

        logger.info("Query \"" + query + "\" has been split into keywords (" + String.join(", ", keywords) + ")");
        return keywords;
    }

    public int addMuseum(String name, String wikiLink, String website, String location, float lat, float lng, String description, String address, String category) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        ResultSet resultSet = null;
        int museumId = -1;

        logger.info("Adding a new museum (" + name + ")...");
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO museums values (default, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING museum_id")) {
            statement.setString(1, name);
            statement.setString(2, wikiLink);
            statement.setString(3, website);
            statement.setString(4, location);
            statement.setFloat(5, lat);
            statement.setFloat(6, lng);
            statement.setString(7, description);
            statement.setString(8, address);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                museumId = resultSet.getInt(1);
            addMuseumCategory(museumId, category);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Added new museum (" + name + ")");
        return museumId;
    }

    public void modifyMuseum(int museumId, String name, String wikiLink, String website, String location, float lat, float lng, String description, String address, String category) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Modifying museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("UPDATE museums values SET name = ?, wiki_link = ?, website = ?, location = ?, lat = ?, lng = ?, description = ?, address = ? WHERE museum_id = ?")) {
            statement.setString(1, name);
            statement.setString(2, wikiLink);
            statement.setString(3, website);
            statement.setString(4, location);
            statement.setFloat(5, lat);
            statement.setFloat(6, lng);
            statement.setString(7, description);
            statement.setString(8, address);
            statement.setInt(9, museumId);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
            removeCategories(museumId);
            addMuseumCategory(museumId, category);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Museum modified");
    }

    public final void addMuseumCategory(int museumId, String category) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Adding category " + category + " to museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO museum_category (fk_museum_id, fk_category_id) SELECT ?, category_id FROM categories WHERE name = ?")) {
            statement.setInt(1, museumId);
            statement.setString(2, category);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Category added");
    }

    private void removeCategories(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Removing categories of museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM museum_category WHERE fk_museum_id = ?")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Categories removed");
    }

    public void removeMuseum(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Removing museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM museums WHERE museum_id = ?")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Removed museum with id " + museumId);
    }

    public Museum getMuseum(int museumId, ArrayList<Review> reviews, ArrayList<Event> events, float ticketPrice) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Retrieving museum with id " + museumId + "...");
        ResultSet resultSet = null;
        Museum museum;

        try (PreparedStatement statement = connection.prepareStatement("SELECT museum_id, m.name, wiki_link, " +
                "website, location, lat, lng, description, address, string_agg(c.name, ', '), thumb_url " +
                "FROM museums m LEFT JOIN museum_category mc on m.museum_id = mc.fk_museum_id " +
                "LEFT JOIN categories c on c.category_id = mc.fk_category_id " +
                "LEFT JOIN museum_image mi on m.museum_id = mi.fk_museum_id WHERE museum_id = ? " +
                "GROUP BY museum_id, m.name, wiki_link, website, location, lat, lng, description, address, thumb_url")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                museum = new Museum.Builder(resultSet.getInt(1), resultSet.getString(2)).
                        wikiLink(resultSet.getString(3))
                        .website(resultSet.getString(4))
                        .location(resultSet.getString(5))
                        .lat(resultSet.getDouble(6))
                        .lng(resultSet.getDouble(7))
                        .description(resultSet.getString(8))
                        .address(resultSet.getString(9))
                        .categories(resultSet.getString(10))
                        .imageUrl(resultSet.getString(11))
                        .build();
                museum.setReviews(reviews);
                museum.setEvents(events);
                museum.setTicketPrice(ticketPrice);
            } else throw new IllegalArgumentException("Provided id doesn't match any museum in the database");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Museum retrieved");
        return museum;
    }

    public ArrayList<String> getCategories() throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        ArrayList<String> categories = new ArrayList<>();

        logger.info("Retrieving categories...");
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT name FROM categories")) {
            while (resultSet.next()) {
                categories.add(resultSet.getString(1));
            }
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Categories retrieved");
        return categories;
    }

    public void addOwner(String ownerMail, int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Adding a new owner " + ownerMail + " to museum (" + museumId + ")...");
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO user_museum SELECT id,? FROM users WHERE email=?")) {
            statement.setInt(1, museumId);
            statement.setString(2, ownerMail);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Added successfully a new owner " + ownerMail + " to museum (" + museumId + ")...");
    }

    public void storeBooking(int userId, int museumId, java.util.Date date, int participants, String additionalNotes) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Storing booking...");
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO bookings (booking_id, fk_user_id, visit_date, participants_number, additional_notes, fk_museum_id) VALUES(default, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, userId);
            statement.setDate(2, sqlDate);
            statement.setInt(3, participants);
            statement.setString(4, additionalNotes);
            statement.setInt(5, museumId);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Booking stored correctly");
    }

    public int getFutureBookingsNumber(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        int bookings = 0;
        ResultSet resultSet = null;

        logger.info("Checking future bookings number for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM bookings WHERE fk_museum_id = ? AND visit_date > current_date")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                bookings = resultSet.getInt(1);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Future bookings number is " + bookings);
        return bookings;
    }

    public int getTotalBookingsNumber(int museumId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        int bookings = 0;
        ResultSet resultSet = null;

        logger.info("Checking bookings number for museum with id " + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM bookings WHERE fk_museum_id = ?")) {
            statement.setInt(1, museumId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                bookings = resultSet.getInt(1);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Bookings number is " + bookings);
        return bookings;
    }
}
