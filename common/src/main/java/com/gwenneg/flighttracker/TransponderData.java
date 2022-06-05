package com.gwenneg.flighttracker;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class TransponderData {

    private String identification;
    private Map<String, Double> position;
    private double trackAngle;
    private double groundSpeed;
    private boolean landed;
}
