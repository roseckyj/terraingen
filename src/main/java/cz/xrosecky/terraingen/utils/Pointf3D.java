package cz.xrosecky.terraingen.utils;

import org.postgis.Point;

public class Pointf3D {
    public final double x;
    public final double y;
    public final double z;

    public Pointf3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Pointf3D(Point p) {
        this.x = p.y;
        this.y = p.z;
        this.z = p.x;
    }

    public Pointf3D(Pointf2D p, double y) {
        this.x = p.x;
        this.y = y;
        this.z = p.z;
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

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    public Pointf2D xz() {
        return new Pointf2D(x, z);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Pointf3D)){
            return false;
        }

        Pointf3D other_ = (Pointf3D) other;

        return other_.x - this.x + other_.y - this.y + other_.z - this.z < 0.001;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (int)Math.floor(prime * result + x * 10000);
        result = (int)Math.floor(prime * result + y * 10000);
        result = (int)Math.floor(prime * result + z * 10000);
        return result;
    }
}
