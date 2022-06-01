package com.gwenneg.flighttracker;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;

public class FlightEngine {

    private static final double ONE_HOUR_IN_SECONDS = 3600D;

    private static double calculateDistance(double distanceX, double distanceY) {
        return Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }

    private final Point departure;
    private final Point arrival;
    private final String aircraft;
    private final double speed; // Arbitrary unit per hour.
    private final double distanceX;
    private final double distanceY;
    private final double distance;
    private final LocalDateTime startTime = LocalDateTime.now(UTC);
    private boolean landed;
    private final double heading;

    public FlightEngine(Point departure, Point arrival, String aircraft, double speed) {
        this.departure = departure;
        this.arrival = arrival;
        this.aircraft = aircraft;
        this.speed = speed;
        distanceX = arrival.getX() - departure.getX();
        distanceY = arrival.getY() - departure.getY();
        distance = calculateDistance(distanceX, distanceY);
        heading = Math.toDegrees(Math.atan2(distanceY, distanceX) - Math.atan2(-distance, 0));
    }

    public boolean isLanded() {
        return landed;
    }

    public double getHeading() {
        return heading;
    }

    public Point getCurrentPoint() {
        double distanceTraveled = speed * SECONDS.between(startTime, LocalDateTime.now(UTC)) / ONE_HOUR_IN_SECONDS;
        // Calculations based on the pythagorean theorem.

        double currentX = departure.getX() + distanceTraveled * distanceX / distance;
        double currentY = departure.getY() + distanceTraveled * distanceY / distance;

        if (departure.getX() <= arrival.getX() && currentX >= arrival.getX() ||
                departure.getX() >= arrival.getX() && currentX <= arrival.getX()) {
            landed = true;
            return new Point(arrival.getX(), arrival.getY());
        } else {
            return new Point(currentX, currentY);
        }
    }

    public String getAircraft() {
        return aircraft;
    }

    @Override
    public String toString() {
        return "Flight{departure=" + departure + ", arrival=" + arrival + ", aircraft=" + aircraft + ", speed=" + speed + ", startTime=" + startTime + "}";
    }
}
