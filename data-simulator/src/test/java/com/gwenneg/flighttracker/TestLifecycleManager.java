package com.gwenneg.flighttracker;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;

import java.util.HashMap;
import java.util.Map;

public class TestLifecycleManager implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> properties = new HashMap<>();
        properties.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory("radar-data"));
        properties.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory("transponder-data"));
        return properties;
    }

    @Override
    public void stop() {
    }
}
