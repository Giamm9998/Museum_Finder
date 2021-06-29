package gateway;

import model.Log;
import model.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ReportGateway {
    static private final Logger logger = Log.getInstance().getLogger();
    private static ReportGateway instance = null;


    public static ReportGateway getInstance() {
        logger.info("Retrieving ReportGateway instance...");
        if (instance == null) {
            logger.info("ReportGateway first initialization...");
            instance = new ReportGateway();
        }
        logger.info("ReportGateway instance retrieved");
        return instance;
    }

    public void report(int userId, int museumId, String reportDescription, boolean verified) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Reporting an error for museum with id" + museumId + "...");
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO reports (report_id, fk_user_id, fk_museum_id, report_description, verified) " +
                        "VALUES(default, ?, ?, ?, ?)")) {
            statement.setInt(1, userId);
            statement.setInt(2, museumId);
            statement.setString(3, reportDescription);
            statement.setBoolean(4, verified);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Error for museum with id" + museumId + " has been reported");
    }

    public void approveReport(int reportId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Approving report with id" + reportId + "...");
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE reports SET verified = TRUE WHERE report_id = ?")) {
            statement.setInt(1, reportId);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Report with id" + reportId + " has been approved");
    }

    public void resolveReport(int reportId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        logger.info("Deleting report with id" + reportId + "...");
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM reports WHERE report_id = ?")) {
            statement.setInt(1, reportId);
            logger.fine("QUERY: " + statement.toString());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Report with id" + reportId + " has been deleted");
    }

    public final void rejectReport(int reportId) throws SQLException {
        logger.info("Rejecting report with id" + reportId + "...");
        resolveReport(reportId);
    }

    public ArrayList<Report> getOwnerReports(int userId) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        ArrayList<Report> reports = new ArrayList<>();
        ResultSet resultSet = null;

        logger.info("Retrieving all unverified reports for user with id " + userId + "...");
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT report_id, report_description, r.fk_museum_id FROM reports r " +
                        "JOIN user_museum um on r.fk_museum_id = um.fk_museum_id " +
                        "WHERE verified IS FALSE AND um.fk_user_id = ?")) {
            statement.setInt(1, userId);
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                reports.add(new Report(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
            }
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Reports retrieved");
        return reports;
    }

    public ArrayList<Report> getAllReports() throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        ArrayList<Report> reports = new ArrayList<>();
        ResultSet resultSet = null;

        logger.info("Retrieving all verified reports...");
        try (PreparedStatement statement = connection.prepareStatement("SELECT report_id, report_description, fk_museum_id FROM reports WHERE verified IS TRUE")) {
            logger.fine("QUERY: " + statement.toString());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                reports.add(new Report(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
            }
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception ignored) {
            }
            ConnectionPool.releaseConnection(connection);
        }
        logger.info("Reports retrieved");
        return reports;
    }
}
