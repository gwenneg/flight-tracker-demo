package com.gwenneg.flighttracker;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RadarData {

    private String aircraftIdentification;
    private double x;
    private double y;
    private double trackAngle;
    private String radarType;
    private boolean landed;
}
