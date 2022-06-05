package com.gwenneg.flighttracker;

import lombok.Getter;
import lombok.Setter;

import static com.gwenneg.flighttracker.FlightSource.ADSB;
import static com.gwenneg.flighttracker.FlightSource.RADAR;

@Getter
@Setter
public class Output {
    private FlightSource source;
    private String aircraft;
    private double x;
    private double y;
    private double trackAngle;
    private boolean landed;

    public Output() {

    }

    public Output(FlightSource source) {
        this.source = source;
    }

    public Output pickBest(Output output) {
        return this;
    }

    public static Output fromAdsbFlight(TransponderData flight) {


        Output output = new Output(ADSB);
        output.setAircraft(flight.getIdentification());
        output.setX(flight.getPosition().get("x"));
        output.setY(flight.getPosition().get("y"));
        output.setTrackAngle(flight.getTrackAngle());
        output.setLanded(flight.isLanded());
        return output;



    }

    public static Output fromRadarFlight(RadarData flight) {

        Output output = new Output(RADAR);
        output.setAircraft(flight.getAircraftIdentification());
        output.setX(flight.getX());
        output.setY(flight.getY());
        output.setTrackAngle(flight.getTrackAngle());
        output.setLanded(flight.isLanded());
        return output;



    }
}
