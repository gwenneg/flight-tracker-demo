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

import static com.gwenneg.flighttracker.FlightData.TRANSPONDER_SOURCE;
import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

@ApplicationScoped
public class TopologyProducer {

    public static final String RADAR_DATA_TOPIC = "radar-data";
    public static final String TRANSPONDER_DATA_TOPIC = "transponder-data";
    public static final String FLIGHT_DATA_TOPIC = "flight-data";

    @Produces
    public Topology produceTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, FlightData> radarStream = builder
                .stream(RADAR_DATA_TOPIC, Consumed.with(Serdes.String(), new ObjectMapperSerde<>(RadarData.class)))
                .map((key, value) -> {
                    FlightData flightData = FlightData.fromRadarData(value);
                    return new KeyValue<>(flightData.getAircraft(), flightData);
                });

        KStream<String, FlightData> transponderStream = builder
                .stream(TRANSPONDER_DATA_TOPIC, Consumed.with(Serdes.String(), new ObjectMapperSerde<>(TransponderData.class)))
                .map((key, value) -> {
                    FlightData flightData = FlightData.fromTransponderData(value);
                    return new KeyValue<>(flightData.getAircraft(), flightData);
                });

        radarStream.merge(transponderStream)
                .groupByKey(Grouped.with(Serdes.String(), new ObjectMapperSerde<>(FlightData.class)))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(1L)))
                .reduce((previousData, newData) -> {
                    // Transponder data is more reliable than radar data.
                    if (newData.getSource().equals(TRANSPONDER_SOURCE) || !previousData.getSource().equals(TRANSPONDER_SOURCE)) {
                        return newData;
                    } else {
                        return previousData;
                    }
                })
                // Kafka Streams may not emit the event from the last window because of this.
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .to(FLIGHT_DATA_TOPIC);

        return builder.build();
    }
}
