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

    public static FlightData fromRadarData(RadarData radarData) {
        FlightData flightData = new FlightData();
        flightData.setSource(RADAR_SOURCE);
        flightData.setAircraft(radarData.getAircraftIdentification());
        flightData.setX(radarData.getX());
        flightData.setY(radarData.getY());
        flightData.setTrackAngle(radarData.getTrackAngle());
        flightData.setLanded(radarData.getLanded());
        return flightData;
    }

    public static FlightData fromTransponderData(TransponderData transponderData) {
        FlightData flightData = new FlightData();
        flightData.setSource(TRANSPONDER_SOURCE);
        flightData.setAircraft(transponderData.getIdentification());
        flightData.setX(transponderData.getPosition().get("x"));
        flightData.setY(transponderData.getPosition().get("y"));
        flightData.setTrackAngle(transponderData.getTrackAngle());
        flightData.setLanded(transponderData.getLanded());
        return flightData;
    }
}
