package gateway;

import model.Review;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewGatewayTest {
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
    @Order(1)
    public void reviewsTest() throws SQLException {
        ReviewGateway reviewGateway = gatewayFactory.getReviewGateway();
        reviewGateway.writeReview(museumId, testInt, testString, testInt);
        ArrayList<Review> reviews = reviewGateway.getReviews(museumId);
        Assertions.assertEquals(1, reviews.size());
        Assertions.assertEquals(testString, reviews.get(0).getText());
        Assertions.assertEquals(testInt, reviews.get(0).getScore());
        int randomScore = ThreadLocalRandom.current().nextInt(1, 5);
        reviewGateway.writeReview(museumId, testInt, testString, randomScore);
        reviews = reviewGateway.getReviews(museumId);
        Assertions.assertEquals(2, reviews.size());
        Assertions.assertEquals((float) (testInt + randomScore) / 2, reviewGateway.getAverageScore(museumId));
        Assertions.assertEquals(2, reviewGateway.getReviewsNumber(museumId));
    }

    @Test
    @Order(2)
    public void reviewSQLInjectionTest() throws SQLException {
        gatewayFactory.getReviewGateway().writeReview(museumId, 1, "prova, 1); DROP TABLE reviews --", 5);
        Assertions.assertNotEquals(0, gatewayFactory.getReviewGateway().getReviews(museumId).size());
    }

}