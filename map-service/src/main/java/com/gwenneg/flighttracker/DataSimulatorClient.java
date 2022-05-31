package com.gwenneg.flighttracker;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/simulate")
@RegisterRestClient(configKey = "data-simulator")
public interface DataSimulatorClient {

    @PUT
    @Consumes(APPLICATION_JSON)
    Uni<Void> simulateFlightData(PointToPointFlight flight);
}
