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

@Path("/flight")
public class FlightResource {

    @Inject
    @RestClient
    AdsbSourceClient adsbSourceClient;

    @Inject
    @RestClient
    RadarSourceClient radarSourceClient;

    @PUT
    @Consumes(APPLICATION_JSON)
    public Uni<Void> startFlight(@Valid Flight flight) {
        // TO SAY Show imperative code (panache orm) then transform it into reactive
        return findAirport(flight.getDeparture())
                .onItem().transformToUni(departure -> {
                    return findAirport(flight.getArrival())
                            .onItem().transformToUni(arrival -> {
                                Point departurePoint = new Point(departure.getX(), departure.getY());
                                Point arrivalPoint = new Point(arrival.getX(), arrival.getY());
                                PointToPointFlight p2pFlight = new PointToPointFlight(departurePoint, arrivalPoint, flight.getAircraft(), flight.getSpeed());

                                switch (flight.getSource()) {
                                    case "ads-b":
                                        return adsbSourceClient.startFlight(p2pFlight);
                                    case "radar":
                                        return radarSourceClient.startFlight(p2pFlight);
                                    default:
                                        throw new BadRequestException("Unexpected source: " + flight.getSource());
                                }
                            });
        });
    }

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @GET
    @Produces(APPLICATION_JSON)
    @CacheResult(cacheName = "airports") // explain what happens when Uni
    public Uni<List<Airport>> getAirports() {
        // TO SAY: never share a session between threads
        return sessionFactory.withSession(session -> {
           return session.createQuery("FROM Airport", Airport.class)
                   .getResultList();
        });
    }

    private static Uni<Airport> findAirport(String code) {
        try {
            return Airport.find("code", code).singleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("Airport not found: " + code);
        }
    }
}
