package com.gwenneg.flighttracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/flight")
public class FlightResource {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Channel("transponder-data")
    Emitter<String> emitter;

    private final Map<String, FlightDataSimulator> movingAircrafts = new ConcurrentHashMap<>();

    @PUT
    @Consumes(APPLICATION_JSON)
    public void startFlight(PointToPointFlight flight) {
        if (movingAircrafts.containsKey(flight)) {
            throw new BadRequestException("Aircraft is already flying");
        }
        FlightDataSimulator movingAircraft = new FlightDataSimulator(flight.getDeparture(), flight.getArrival(), flight.getSpeed());
        movingAircrafts.put(flight.getAircraft(), movingAircraft);
    }

    @Scheduled(every = "PT1S")
    void updateFlightPositions() {
        List<String> landed = new ArrayList<>();
        for (Map.Entry<String, FlightDataSimulator> movingAircraft : movingAircrafts.entrySet()) {

            if (movingAircraft.getValue().isLanded()) {
                landed.add(movingAircraft.getKey());
            }

            TransponderData data = buildRadarData(movingAircraft.getKey(), movingAircraft.getValue());
            try {
                emitter.send(objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Transponder data serialization failed", e);
            }

        }
        for (String aircraft :landed) {
            movingAircrafts.remove(aircraft);
        }
    }

    private static TransponderData buildRadarData(String aircraft, FlightDataSimulator flightDataSimulator) {
        TransponderData data = new TransponderData();
        data.setIdentification(aircraft);
        data.setPosition(Map.of(
                "x", flightDataSimulator.getPosition().getX(),
                "y", flightDataSimulator.getPosition().getY()
        ));
        data.setTrackAngle(flightDataSimulator.getTrackAngle());
        data.setLanded(flightDataSimulator.isLanded());
        return data;
    }
}
