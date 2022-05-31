package com.gwenneg.flighttracker;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/frontend")
public class FrontendResource {

    @Inject
    @RestClient
    DataSimulatorClient dataSimulatorClient;

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @PUT
    @Path("/simulate")
    @Consumes(APPLICATION_JSON)
    public Uni<Void> startFlight(@Valid AirportToAirportFlight a2aFlight) {
        if (a2aFlight.getDeparture().equals(a2aFlight.getArrival())) {
            throw new BadRequestException("Departure and arrival airports must be different");
        }
        return findAirport(a2aFlight.getDeparture())
                .onItem().transformToUni(departure -> {
                    return findAirport(a2aFlight.getArrival())
                            .onItem().transformToUni(arrival -> {
                                Point departurePoint = new Point(departure.getX(), departure.getY());
                                Point arrivalPoint = new Point(arrival.getX(), arrival.getY());
                                PointToPointFlight p2pFlight = new PointToPointFlight(
                                        a2aFlight.getSource(),
                                        departurePoint,
                                        arrivalPoint,
                                        a2aFlight.getAircraft(),
                                        a2aFlight.getSpeed()
                                );
                                return dataSimulatorClient.simulateFlightData(p2pFlight);
                            });
        });
    }

    @GET
    @Path("/airports")
    @Produces(APPLICATION_JSON)
    @CacheResult(cacheName = "airports")
    public Uni<List<Airport>> getAirports() {
        return sessionFactory.withSession(session -> {
           return session.createQuery("FROM Airport ORDER BY code", Airport.class)
                   .getResultList();
        });
    }

    @CacheResult(cacheName = "airports")
    Uni<Airport> findAirport(String code) {
        try {
            return Airport.find("code", code).singleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("Airport not found: " + code);
        }
    }
}
