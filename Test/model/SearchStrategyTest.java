package model;

import gateway.GatewayFactory;
import gateway.GatewayPSQLFactory;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;

public class SearchStrategyTest {
    private static final GatewayFactory gatewayFactory = new GatewayPSQLFactory();

    @Test
    public void locationTest() throws SQLException {
        System.out.println("Checking if different locations (with a very common query) results in different museums orders...");
        gatewayFactory.getMuseumGateway().setStrategy(new LocationStrategy());
        JSONArray json1 = gatewayFactory.getMuseumGateway().searchMuseums("museo", "Torino");
        JSONArray json2 = gatewayFactory.getMuseumGateway().searchMuseums("museo", "Napoli");
        Assertions.assertNotEquals(json1.toString(), json2.toString());
    }

    @Test
    public void locationFallbackTest() throws SQLException {
        System.out.println("Checking if location strategy falls back to score strategy if no location is retrieved...");
        gatewayFactory.getMuseumGateway().setStrategy(new LocationStrategy());
        JSONArray json1 = gatewayFactory.getMuseumGateway().searchMuseums("museo", null);
        gatewayFactory.getMuseumGateway().setStrategy(new ScoreStrategy());
        JSONArray json2 = gatewayFactory.getMuseumGateway().searchMuseums("museo", null);
        Assertions.assertEquals(json1.toString(), json2.toString());
    }

    @Test
    public void ratingTest() throws SQLException {
        System.out.println("Checking if the rating strategy actually orders museums by rating...");
        gatewayFactory.getMuseumGateway().setStrategy(new RatingStrategy());
        JSONArray json = gatewayFactory.getMuseumGateway().searchMuseums("museo", null);
        for (int i = 1; i < json.length(); i++) {
            Assertions.assertTrue(json.getJSONObject(i).getDouble("average") <= json.getJSONObject(i-1).getDouble("average"));
        }

    }

    @Test
    void performanceTest() {
        System.out.println("Checking if the strategies meet the performance constraint of 5 seconds per query...");
        gatewayFactory.getMuseumGateway().setStrategy(new ScoreStrategy());
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            gatewayFactory.getMuseumGateway().searchMuseums("query molto lunga che può aumentare il tempo di esecuzione", null);
        });

        gatewayFactory.getMuseumGateway().setStrategy(new LocationStrategy());
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            gatewayFactory.getMuseumGateway().searchMuseums("query molto lunga che può aumentare il tempo di esecuzione", "firenze");
        });

        gatewayFactory.getMuseumGateway().setStrategy(new RatingStrategy());
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            gatewayFactory.getMuseumGateway().searchMuseums("query molto lunga che può aumentare il tempo di esecuzione", null);
        });
    }
}
