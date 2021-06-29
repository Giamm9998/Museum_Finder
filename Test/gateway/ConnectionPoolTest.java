package gateway;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

class ConnectionPoolTest {

    @BeforeAll
    static void initialize() throws SQLException {
        ConnectionPool.create();
        Assertions.assertEquals(ConnectionPool.getSize(), 5);
    }

    @AfterAll
    static void empty() throws SQLException {
        ConnectionPool.empty();
        Assertions.assertEquals(ConnectionPool.getSize(), 0);
    }

    @Test
    public void creationTest() throws SQLException {
        //calling create method (has already been called)
        ConnectionPool.create();
        Assertions.assertEquals(ConnectionPool.getSize(), 5);
    }

    @Test
    public void connectionTest() throws SQLException {
        ArrayList<Connection> connections = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            connections.add(ConnectionPool.getConnection());
            Assertions.assertEquals(ConnectionPool.getSize(), 5);
        }

        //getting a connection when all the other ones are used
        Assertions.assertThrows(RuntimeException.class, ConnectionPool::getConnection);

        for (Connection conn : connections) {
            ConnectionPool.releaseConnection(conn);
            Assertions.assertEquals(ConnectionPool.getSize(), 5);
        }

    }

}
