package com.gwenneg.flighttracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@ApplicationScoped
@Path("/simulate")
public class SimulatorService {

    @Inject
    @Channel("radar-data")
    Emitter<String> radarEmitter;

    @Inject
    @Channel("transponder-data")
    Emitter<String> transponderEmitter;

    @Inject
    ObjectMapper objectMapper;

    private final Map</* Aircraft identification */ String, Aircraft> aircrafts = new ConcurrentHashMap<>();

    @PUT
    @Consumes(APPLICATION_JSON)
    public void simulateFlightData(PointToPointFlight flight) {
        if (aircrafts.containsKey(flight.getAircraft())) {
            throw new BadRequestException("Aircraft is already flying: " + flight.getAircraft());
        }
        Aircraft aircraft = Aircraft.fromPointToPointFlight(flight);
        aircrafts.put(flight.getAircraft(), aircraft);
    }

    @Scheduled(every = "1s", delayed = "30s")
    void emitFlightData() {
        for (Map.Entry<String, Aircraft> aircraft : aircrafts.entrySet()) {
            try {
                switch (aircraft.getValue().getSource()) {
                    case "radar":
                        RadarData radarData = aircraft.getValue().toRadarData(aircraft.getKey());
                        radarEmitter.send(objectMapper.writeValueAsString(radarData));
                        break;
                    case "transponder":
                        TransponderData transponderData = aircraft.getValue().toTransponderData(aircraft.getKey());
                        transponderEmitter.send(objectMapper.writeValueAsString(transponderData));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected data source: " + aircraft.getValue().getSource());
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Flight data serialization failed", e);
            }
        }
        aircrafts.entrySet().removeIf(entry -> entry.getValue().isLanded());
    }
}
