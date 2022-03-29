package cz.xrosecky.terraingen.utils;

public class Pointf2D {
    public final double x;
    public final double z;

    public Pointf2D(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public double lat() {
        return x;
    }

    public double lon() {
        return z;
    }

    @Override
    public String toString() {
        return x + " " + z;
    }

    public String toLatLonString() {
        return z + " " + x;
    }
}