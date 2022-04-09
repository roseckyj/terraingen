package cz.xrosecky.terraingen.utils;

import org.postgis.Point;

public class Pointf2D {
    public final double x;
    public final double z;

    public Pointf2D(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public Pointf2D(Point p) {
        this.x = p.y;
        this.z = p.x;
    }

    public double lat() {
        return x;
    }

    public double lon() {
        return z;
    }

    public double length() {
        return Math.sqrt(x * x + z * z);
    }

    @Override
    public String toString() {
        return x + " " + z;
    }

    public String toLatLonString() {
        return z + " " + x;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Pointf2D)){
            return false;
        }

        Pointf2D other_ = (Pointf2D) other;

        return other_.x - this.x + other_.z - this.z < 0.001;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (int)Math.floor(prime * result + x * 10000);
        result = (int)Math.floor(prime * result + z * 10000);
        return result;
    }
}