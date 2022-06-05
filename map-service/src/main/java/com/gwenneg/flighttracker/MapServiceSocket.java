package com.gwenneg.flighttracker;

import io.quarkus.logging.Log;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;

@ServerEndpoint("/positions")
@ApplicationScoped
public class MapServiceSocket {

    private static final String FLIGHT_DATA_TOPIC = "flight-data";

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        Log.debugf("Session created: %s", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        Log.debugf("Session closed: %s", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session.getId());
        Log.debugf("Session closed on error: %s", session.getId());
    }

    @Incoming(FLIGHT_DATA_TOPIC)
    public void broadcast(String aircraftPosition) {
        Log.debugf("Broadcasting aircraft position: %s", aircraftPosition);
        sessions.values().forEach(session -> {
            session.getAsyncRemote().sendObject(aircraftPosition, result ->  {
                if (result.getException() != null) {
                    Log.error("Unable to send message", result.getException());
                }
            });
        });
    }
}
