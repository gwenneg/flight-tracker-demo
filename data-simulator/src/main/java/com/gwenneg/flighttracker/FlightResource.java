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
public class FlightResource {

    @Inject
    @Channel("radar-data")
    Emitter<String> radarEmitter;

    @Inject
    @Channel("transponder-data")
    Emitter<String> transponderEmitter;

    @Inject
    ObjectMapper objectMapper;

    private final Map<String, FlightDataSimulator> movingAircrafts = new ConcurrentHashMap<>();

    @PUT
    @Consumes(APPLICATION_JSON)
    public void simulateFlightData(PointToPointFlight flight) {
        if (movingAircrafts.containsKey(flight)) {
            throw new BadRequestException("Aircraft is already flying");
        }
        FlightDataSimulator movingAircraft = new FlightDataSimulator(flight.getSource(), flight.getDeparture(), flight.getArrival(), flight.getSpeed());
        movingAircrafts.put(flight.getAircraft(), movingAircraft);
    }

    @Scheduled(every = "PT0.5S")
    void emitFlightData() {
        for (Map.Entry<String, FlightDataSimulator> movingAircraft : movingAircrafts.entrySet()) {
            try {
                switch (movingAircraft.getValue().getSource()) {
                    case "radar":
                        RadarData radarData = buildRadarData(movingAircraft.getKey(), movingAircraft.getValue());
                        radarEmitter.send(objectMapper.writeValueAsString(radarData));
                        break;
                    case "transponder":
                        TransponderData transponderData = buildTransponderData(movingAircraft.getKey(), movingAircraft.getValue());
                        transponderEmitter.send(objectMapper.writeValueAsString(transponderData));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected data source: " + movingAircraft.getValue().getSource());
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Flight data serialization failed", e);
            }
        }
        movingAircrafts.entrySet().removeIf(entry -> entry.getValue().isLanded());
    }

    private static RadarData buildRadarData(String aircraft, FlightDataSimulator flightEngine) {
        RadarData data = new RadarData();
        data.setAircraftIdentification(aircraft);
        data.setX(flightEngine.getPosition().getX());
        data.setY(flightEngine.getPosition().getY());
        data.setTrackAngle(flightEngine.getTrackAngle());
        data.setLanded(flightEngine.isLanded());
        return data;
    }

    private static TransponderData buildTransponderData(String aircraft, FlightDataSimulator flightDataSimulator) {
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
