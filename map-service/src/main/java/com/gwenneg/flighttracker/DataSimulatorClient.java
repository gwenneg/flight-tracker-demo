package com.gwenneg.flighttracker;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/simulate")
@RegisterRestClient(configKey = "data-simulator")
public interface DataSimulatorClient {

    @PUT
    Uni<Void> simulateFlightData(PointToPointFlight flight);
}
