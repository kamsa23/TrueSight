package me.kamsa23.trueSight.utils;

public class MathUtils {
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static double distanceSquared(double x1, double y1, double z1,
                                         double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }
}