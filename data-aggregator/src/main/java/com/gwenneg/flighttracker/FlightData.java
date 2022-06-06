package com.gwenneg.flighttracker;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FlightData {

    public static final String RADAR_SOURCE = "radar";
    public static final String TRANSPONDER_SOURCE = "transponder";

    private String source;
    private String aircraft;
    private Double x;
    private Double y;
    private Double trackAngle;
    private Boolean landed;

    public static FlightData fromRadarData(RadarData data) {
        FlightData flightData = new FlightData();
        flightData.setSource(RADAR_SOURCE);
        flightData.setAircraft(data.getAircraftIdentification());
        flightData.setX(data.getX());
        flightData.setY(data.getY());
        flightData.setTrackAngle(data.getTrackAngle());
        flightData.setLanded(data.getLanded());
        return flightData;
    }

    public static FlightData fromTransponderData(TransponderData data) {
        FlightData flightData = new FlightData();
        flightData.setSource(TRANSPONDER_SOURCE);
        flightData.setAircraft(data.getIdentification());
        flightData.setX(data.getPosition().get("x"));
        flightData.setY(data.getPosition().get("y"));
        flightData.setTrackAngle(data.getTrackAngle());
        flightData.setLanded(data.getLanded());
        return flightData;
    }
}
