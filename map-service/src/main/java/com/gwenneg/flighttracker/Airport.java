package com.gwenneg.flighttracker;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@ToString
@EqualsAndHashCode
public class Airport extends PanacheEntityBase {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private String code;

    private double x;
    private double y;
}
