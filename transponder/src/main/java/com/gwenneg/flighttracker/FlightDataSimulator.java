package com.gwenneg.flighttracker;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;

public class FlightDataSimulator {

    private static final double ONE_HOUR_IN_SECONDS = 3600D;

    private final Point departure;
    private final Point arrival;
    private final double speed; // Arbitrary unit per hour.
    private final double distanceX;
    private final double distanceY;
    private final double distance;
    private final LocalDateTime startTime = LocalDateTime.now(UTC);
    private boolean landed;
    private final double trackAngle;

    public FlightDataSimulator(Point departure, Point arrival, double speed) {
        this.departure = departure;
        this.arrival = arrival;
        this.speed = speed;
        distanceX = arrival.getX() - departure.getX();
        distanceY = arrival.getY() - departure.getY();
        distance = MathUtils.calculateDistance(distanceX, distanceY);
        trackAngle = MathUtils.calculateTrackAngle(distanceX, distanceY, -distance);
    }

    public boolean isLanded() {
        return landed;
    }

    public double getTrackAngle() {
        return trackAngle;
    }

    public Point getPosition() {
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
}
