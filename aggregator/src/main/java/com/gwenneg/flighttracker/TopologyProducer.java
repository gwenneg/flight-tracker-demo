package com.gwenneg.flighttracker;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;

import java.time.Duration;

import static com.gwenneg.flighttracker.FlightSource.ADSB;
import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

@ApplicationScoped
public class TopologyProducer {

    public static final String ADSB_TOPIC = "ads-b";
    public static final String RADAR_TOPIC = "radar";
    public static final String TEMPERATURES_AGGREGATED_TOPIC = "temperatures-aggregated";

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Output> adsb = builder
                .stream(ADSB_TOPIC, Consumed.with(Serdes.String(), new ObjectMapperSerde<>(TransponderData.class)))
                .map((key, value) -> {
                    Output output = Output.fromAdsbFlight(value);
                    return new KeyValue<>(output.getAircraft(), output);
                });

        KStream<String, Output> radar = builder
                .stream(RADAR_TOPIC, Consumed.with(Serdes.String(), new ObjectMapperSerde<>(RadarData.class)))
                .map((key, value) -> {
                    Output output = Output.fromRadarFlight(value);
                    return new KeyValue<>(output.getAircraft(), output);
                });

        adsb.merge(radar)
                .groupByKey(Grouped.with(Serdes.String(), new ObjectMapperSerde<>(Output.class)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(1L)))
                .reduce((old, neww) -> {






                       /*
    TODO


    TODO déterminer ce qui fait qu'un event est le meilleur:
     adsb > radar > l'autre truc?
     timestamp fourni en input ? on garde le plus récent
     distance du radar à l'aéronef
     */


                    if (old.getSource() == ADSB && neww.getSource() != ADSB) {
                        return old;
                    } else {
                        return neww;
                    }
                })
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .to(TEMPERATURES_AGGREGATED_TOPIC);

        return builder.build();
    }
}