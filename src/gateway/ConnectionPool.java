package gateway;

import model.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ConnectionPool {
    static private final Logger logger = Log.getInstance().getLogger();
    static private final ArrayList<Connection> usedConnections = new ArrayList<>();
    private static final int MAX_POOL_SIZE = 5;
    static private ArrayList<Connection> connectionPool = new ArrayList<>();

    public static void create() throws SQLException {
        if (connectionPool.isEmpty()) {
            logger.info("Providing " + MAX_POOL_SIZE + " valid connections to the connection pool...");
            connectionPool = new ArrayList<>(MAX_POOL_SIZE);
            for (int i = 0; i < MAX_POOL_SIZE; i++) {
                connectionPool.add(createConnection());
            }
            logger.info("Connections provided");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection());
            } else {
                throw new RuntimeException("Maximum pool size reached, no available connections");
            }
        }

        Connection connection = connectionPool.remove(connectionPool.size() - 1);

        if (!connection.isValid(1)) {
            logger.info("Connection retrieved is not valid anymore, replacing with a new one...");
            connection = createConnection();
        }

        usedConnections.add(connection);
        logger.info("Connection retrieved");
        return connection;
    }

    public static void releaseConnection(Connection connection) {
        connectionPool.add(connection);
        usedConnections.remove(connection);
        logger.info("Connection released");
    }

    private static Connection createConnection() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            ip="" // ip of the database server
            connection = DriverManager.getConnection("jdbc:postgresql://"+ip+"/museums", "admin", "admin");
        } catch (ClassNotFoundException e) {
            logger.severe(Log.getStringStackTrace(e));
            System.exit(1);
        }
        logger.info("Connection created");
        return connection;
    }

    public static int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    public static void empty() throws SQLException {
        logger.info("Emptying connection pool and closing all connections...");
        for (Connection c : usedConnections) {
            releaseConnection(c);
        }
        for (Connection c : connectionPool) {
            c.close();
        }
        connectionPool.clear();
        logger.info("Connection pool emptied");
    }
}
