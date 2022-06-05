package com.gwenneg.flighttracker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@ToString
public class Flight {

    @NotNull
    private String aircraft;

    @NotNull
    private Double speed;

    @NotNull
    private String departure;

    @NotNull
    private String arrival;

    @NotNull
    private String source;
}
