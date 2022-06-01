package com.gwenneg.flighttracker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TransponderData {

    private String aircraft;
    private double x;
    private double y;
    private double heading;
    private boolean landed;
}
