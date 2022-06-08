package com.gwenneg.flighttracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestLifecycleManager.class)
public class SimulatorServiceTest {

    @Inject
    @Any
    InMemoryConnector inMemoryConnector;

    @Inject
    ObjectMapper objectMapper;

    InMemorySink<String> inMemorySink;

    @PostConstruct
    void postConstruct() {
        inMemorySink = inMemoryConnector.sink("radar-data");
    }

    @Test
    void test() {
        PointToPointFlight flight = new PointToPointFlight("radar", new Point(10D, 10D), new Point(100D, 100D), "F-GGOB", 10D);

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(flight)
                .when().put("/simulate")
                .then().statusCode(204);

        await().atMost(Duration.ofMinutes(1L)).until(() -> radarDataEmitted(flight.getAircraft()));
    }

    private boolean radarDataEmitted(String expectedAircraft) {
        if (inMemorySink.received().isEmpty()) {
            return false;
        } else {
            Message<String> message = inMemorySink.received().get(0);
            try {
                RadarData radarData = objectMapper.readValue(message.getPayload(), RadarData.class);
                assertEquals(expectedAircraft, radarData.getAircraftIdentification());
                return true;
            } catch (JsonProcessingException e) {
                Log.error("RadarData deserialization failed", e);
                return false;
            }
        }
    }
}
