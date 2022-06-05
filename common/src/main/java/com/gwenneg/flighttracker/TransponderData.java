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
    private Double trackAngle;
    private Double groundSpeed;
    private Boolean landed;
}
