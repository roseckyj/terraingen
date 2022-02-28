package cz.xrosecky.terraingen.utils;

public class Pointf3D {
    public final double x;
    public final double y;
    public final double z;

    public Pointf3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double lat() {
        return x;
    }

    public double lon() {
        return y;
    }

    public double alt() {
        return z;
    }
}
