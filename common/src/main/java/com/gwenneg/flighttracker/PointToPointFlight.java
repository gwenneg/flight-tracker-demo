package com.gwenneg.flighttracker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class PointToPointFlight {

    private Point departure;
    private Point arrival;
    private String aircraft;
    private Double speed;
}
