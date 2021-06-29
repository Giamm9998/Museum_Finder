package gateway;

import gateway.*;
import model.Event;
import model.Museum;
import model.Review;
import org.json.JSONArray;
import org.junit.jupiter.api.*;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MuseumGatewayTest {
    private static final GatewayFactory gatewayFactory = new GatewayPSQLFactory();
    private static final String testString = "test";
    private static final float testFloat = 1;
    private static final int testInt = 1;
    private static int museumId;


    @BeforeAll
    static void initialize() throws SQLException {
        System.out.println("Starting database...");
        System.out.println("Adding Test Museum");
        museumId = gatewayFactory.getMuseumGateway().addMuseum(testString, testString, testString, testString, testFloat, testFloat, testString, testString, testString);
        System.out.println("Added Test Museum with id: " + museumId);

    }

    @AfterAll
    static void close() throws SQLException {
        System.out.println("Removing test museum");
        gatewayFactory.getMuseumGateway().removeMuseum(museumId);
        System.out.println("Closing database");
    }

    @Test
    public void singletonTest() {
        System.out.println("Checking if getInstance method returns always the same instance...");
        MuseumGateway museumGateway1 = MuseumGateway.getInstance();
        MuseumGateway museumGateway2 = MuseumGateway.getInstance();
        Assertions.assertEquals(museumGateway1, museumGateway2);
    }

    @Test
    public void searchMuseumsTest() throws SQLException {
        //Test void results
        Assertions.assertThrows(NullPointerException.class, () -> gatewayFactory.getMuseumGateway().searchMuseums("skdcsjndscdkslcdjchsd", null));
        //Test normal research
        JSONArray a1 = gatewayFactory.getMuseumGateway().searchMuseums("museo", null);
        if (a1 != null) {
            Assertions.assertFalse(a1.isEmpty());
        }
    }

    @Test
    public void getMuseumTest() {
        //Test functional requirement of getMuseums()
        Museum museum = null;
        int id = testInt;
        try {
            museum = gatewayFactory.getMuseumGateway().getMuseum(id, new ArrayList<>(), new ArrayList<>(), 0);
        } catch (SQLException e) {
            System.out.println("This museum is not in the database, change id");
        }
        if (museum != null) {
            Assertions.assertEquals(id, museum.getMuseumId());
        }
        //Test that if id provided is not in db the method throws an exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> gatewayFactory.getMuseumGateway().getMuseum(-1, new ArrayList<>(), new ArrayList<>(), 0));
    }

    @Test
    public void modifyMuseumTest() throws SQLException {
        String newChar = "!";
        gatewayFactory.getMuseumGateway().modifyMuseum(museumId, testString + newChar, testString + newChar, testString + newChar, testString + newChar, testFloat + 1, testFloat + 1, testString + newChar, testString + newChar, testString + newChar);
        ArrayList<Review> reviews = gatewayFactory.getReviewGateway().getReviews(museumId);
        ArrayList<Event> events = gatewayFactory.getEventGateway().getEvents(museumId);
        float ticketPrice = gatewayFactory.getEventGateway().getTicketPrice(museumId);
        Museum m = gatewayFactory.getMuseumGateway().getMuseum(museumId, reviews, events, ticketPrice);
        Assertions.assertEquals(testString + newChar, m.getName());
        Assertions.assertEquals(testString + newChar, m.getDescription());
        Assertions.assertEquals(testString + newChar, m.getAddress());
        Assertions.assertEquals(testString + newChar, m.getWebsite());
        Assertions.assertEquals(testString + newChar, m.getWikiLink());
        Assertions.assertEquals(testString + newChar, m.getLocation());
        Assertions.assertEquals(testFloat + 1, m.getLat());
        Assertions.assertEquals(testFloat + 1, m.getLng());
    }


    @Test
    @Order(1)
    public void bookingTest() throws SQLException {
        //creating tomorrow date
        MuseumGateway museumGateway = gatewayFactory.getMuseumGateway();
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();
        museumGateway.storeBooking(testInt, museumId, dt, testInt, testString);
        Assertions.assertEquals(1, museumGateway.getFutureBookingsNumber(museumId));
        museumGateway.storeBooking(testInt, museumId, new Date(), testInt, testString);
        Assertions.assertEquals(1, museumGateway.getFutureBookingsNumber(museumId));
        Assertions.assertEquals(2, museumGateway.getTotalBookingsNumber(museumId));
    }

    @Test
    @Order(2)
    public void bookingSQLInjection() throws SQLException {
        gatewayFactory.getMuseumGateway().storeBooking(1, museumId, new Date(), 1, "prova); DROP TABLE bookings --");
        Assertions.assertNotEquals(0, gatewayFactory.getMuseumGateway().getTotalBookingsNumber(museumId));
    }
}
