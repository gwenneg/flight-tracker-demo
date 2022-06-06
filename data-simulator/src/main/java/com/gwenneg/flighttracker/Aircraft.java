package com.gwenneg.flighttracker;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Aircraft {

    private String source;
    private Point departure;
    private Point arrival;
    private double speed;
    private double deltaX;
    private double deltaY;
    private double distance;
    private double trackAngle;
    private boolean landed;
    private LocalDateTime startTime = LocalDateTime.now(UTC);

    public Aircraft(String source, Point departure, Point arrival, double speed) {
        this.source = source;
        this.departure = departure;
        this.arrival = arrival;
        this.speed = speed;
        deltaX = arrival.getX() - departure.getX();
        deltaY = arrival.getY() - departure.getY();
        distance = MathUtils.calculateDistance(deltaX, deltaY);
        trackAngle = MathUtils.calculateTrackAngle(deltaX, deltaY, -distance);
    }

    public String getSource() {
        return source;
    }

    public Point getPosition() {
        double percentageDone = speed * SECONDS.between(startTime, LocalDateTime.now(UTC)) / distance;
        double currentX = departure.getX() + deltaX * percentageDone;
        double currentY = departure.getY() + deltaY * percentageDone;
        if (percentageDone >= 1d) {
            landed = true;
            return new Point(arrival.getX(), arrival.getY());
        } else {
            return new Point(currentX, currentY);
        }
    }

    public double getTrackAngle() {
        return trackAngle;
    }

    public boolean isLanded() {
        return landed;
    }
}