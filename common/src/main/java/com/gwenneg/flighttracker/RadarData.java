package com.gwenneg.flighttracker;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RadarData {

    private String aircraftIdentification;
    private Double x;
    private Double y;
    private Double trackAngle;
    private String radarType;
    private Boolean landed;
}
