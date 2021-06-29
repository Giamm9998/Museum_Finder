package gateway;

import model.Event;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EventGatewayTest {
    private static final GatewayFactory gatewayFactory = new GatewayPSQLFactory();
    private static final String testString = "test";
    private static final float testFloat = 1;
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
    public void eventsTest() throws SQLException {
        //creating tomorrow date
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();
        EventGateway eventGateway = gatewayFactory.getEventGateway();
        //Typology: 1 discount 2 closed 3 else
        eventGateway.createEvent(museumId, testString, new Date(), dt, 1, 50);
        Assertions.assertEquals(5, eventGateway.getTicketPrice(museumId));
        eventGateway.createEvent(museumId, testString, new Date(), dt, 2, 0);
        Assertions.assertTrue(eventGateway.isClosed(museumId, new Date()));
        eventGateway.createEvent(museumId, testString, new Date(), dt, 3, 0);
        ArrayList<Event> events = eventGateway.getEvents(museumId);
        Assertions.assertEquals(3, events.size());
        Assertions.assertEquals(testString, events.get(2).getDescription());
    }
}
