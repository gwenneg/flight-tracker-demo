package com.gwenneg.flighttracker;

public class MathUtils {

    public static double calculateDistance(double deltaX, double deltaY) {
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    public static double calculateTrackAngle(double deltaX, double deltaY, double distance) {
        return Math.toDegrees(Math.atan2(deltaY, deltaX) - Math.atan2(distance, 0));
    }
}
