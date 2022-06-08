package com.gwenneg.flighttracker;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.vertx.core.json.JsonArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class FrontendResourceTest {

    @Test
    void test() {
        String responseBody = RestAssured
                .when().get("/frontend/airports")
                .then().statusCode(200)
                .extract().asString();
        JsonArray airports = new JsonArray(responseBody);
        assertEquals(22, airports.size());
    }
}
