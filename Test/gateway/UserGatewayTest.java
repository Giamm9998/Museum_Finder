package gateway;

import gateway.*;
import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.naming.SizeLimitExceededException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class UserGatewayTest {
    private static final GatewayFactory gatewayFactory = new GatewayPSQLFactory();
    private static final String email = "test@gmail.com";
    private static final String password = "testPassword";
    private static final String name = "Test";
    private static final String role = "user";
    private static final String testString = "test";
    private static int museumId;

    @BeforeAll
    static void initialize() throws SQLException {
        System.out.println("Starting database...");
        System.out.println("Registering test user...");
        gatewayFactory.getUserGateway().register(email, password, name, name, role);
        System.out.println("Adding Test Museum");
        museumId = gatewayFactory.getMuseumGateway().addMuseum(testString, testString, testString, testString, 1, 1, testString, testString, testString);
        System.out.println("Added Test Museum with id: " + museumId);
    }

    @AfterAll
    static void close() throws SQLException {
        System.out.println("Removing test user...");
        gatewayFactory.getUserGateway().removeUser(email);
        System.out.println("Removing test museum");
        gatewayFactory.getMuseumGateway().removeMuseum(museumId);
        System.out.println("Closing database...");

    }

    @Test
    public void loginSQLInjectionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> gatewayFactory.getUserGateway().login(email + " OR 1=1", "injection OR 1=1"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gatewayFactory.getUserGateway().login(email, "injection; DROP TABLE users --"));
        //checking that table still exist
        Assertions.assertDoesNotThrow(() -> gatewayFactory.getUserGateway().login(email, password));

    }

    @Test
    public void loginTest() {
        Assertions.assertDoesNotThrow(() -> gatewayFactory.getUserGateway().login(email, password));
        Assertions.assertThrows(IllegalArgumentException.class, () -> gatewayFactory.getUserGateway().login("", ""));
    }

    @Test
    public void registerTest() {
        //Test that cannot register with same mail
        System.out.println("Trying to register users with mail already used...");
        Assertions.assertThrows(SQLException.class, () -> gatewayFactory.getUserGateway().register(email, password, name, name, role));
        Assertions.assertDoesNotThrow(() -> gatewayFactory.getUserGateway().login(email, password));
    }

    @Test
    public void singletonTest() {
        System.out.println("Checking if getInstance method returns always the same instance...");
        UserGateway userGateway1 = UserGateway.getInstance();
        UserGateway userGateway2 = UserGateway.getInstance();
        Assertions.assertEquals(userGateway1, userGateway2);
    }

    @Test
    public void accessTest() throws SQLException, SizeLimitExceededException {
        UserGateway userGateway = gatewayFactory.getUserGateway();
        User user = userGateway.login(email, password);
        int userId = user.getId();
        userGateway.access(museumId, userId, new Date());
        Assertions.assertEquals(1, userGateway.getAllTimeAccesses(museumId));
        Assertions.assertEquals(1, userGateway.getDailyAccesses(museumId));
        Assertions.assertEquals(1, userGateway.getAllTimeAccesses(museumId));
        Date date = new GregorianCalendar(2016, Calendar.MARCH, 11).getTime();
        userGateway.access(museumId, userId, date);
        Assertions.assertEquals(2, userGateway.getAllTimeAccesses(museumId));
        Assertions.assertEquals(1, userGateway.getWeeklyAccesses(museumId));
        Assertions.assertEquals(1, userGateway.getDailyAccesses(museumId));
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        date = new Date(System.currentTimeMillis() - (3 * DAY_IN_MS));
        userGateway.access(museumId, userId, date);
        Assertions.assertEquals(3, userGateway.getAllTimeAccesses(museumId));
        Assertions.assertEquals(2, userGateway.getWeeklyAccesses(museumId));
        Assertions.assertEquals(1, userGateway.getDailyAccesses(museumId));
    }

}
