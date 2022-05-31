package com.gwenneg.flighttracker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class PointToPointFlight {

    private String source;
    private Point departure;
    private Point arrival;
    private String aircraft;
    private Double speed;
}
