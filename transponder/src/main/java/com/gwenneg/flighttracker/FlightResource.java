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

    private final Map<String, FlightEngine> movingAircrafts = new ConcurrentHashMap<>();

    @PUT
    @Consumes(APPLICATION_JSON)
    public void startFlight(PointToPointFlight flight) {
        if (movingAircrafts.containsKey(flight)) {
            throw new BadRequestException("Aircraft is already flying");
        }
        FlightEngine movingAircraft = new FlightEngine(flight.getDeparture(), flight.getArrival(), flight.getAircraft(), flight.getSpeed());
        movingAircrafts.put(flight.getAircraft(), movingAircraft);
    }

    @Scheduled(every = "PT1S")
    void updateFlightPositions() {
        List<String> landed = new ArrayList<>();
        for (FlightEngine movingAircraft : movingAircrafts.values()) {
            movingAircraft.getAircraft();
            movingAircraft.getCurrentPoint();

            if (movingAircraft.isLanded()) {
                landed.add(movingAircraft.getAircraft());
            }

            TransponderData data = buildRadarData(movingAircraft);
            try {
                emitter.send(objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
        for (String aircraft :landed) {
            movingAircrafts.remove(aircraft);
        }
    }

    private static TransponderData buildRadarData(FlightEngine flightEngine) {
        TransponderData data = new TransponderData();
        data.setIdentification(flightEngine.getAircraft());
        data.setPosition(Map.of(
                "x", flightEngine.getCurrentPoint().getX(),
                "y", flightEngine.getCurrentPoint().getY()
        ));
        data.setTrackAngle(flightEngine.getHeading());
        data.setLanded(flightEngine.isLanded());
        return data;
    }
}
