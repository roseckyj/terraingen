package cz.xrosecky.terraingen.utils;

public class Point2D {
    public final int x;
    public final int z;

    public Point2D(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public boolean within(Point2D a, Point2D b) {
        return x >= Math.min(a.x, b.x) && x < Math.max(a.x, b.x) && z >= Math.min(a.z, b.z) && z < Math.max(a.z, b.z);
    }

    @Override
    public String toString() {
        return "(" + x + "," + z + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Point2D)){
            return false;
        }

        Point2D other_ = (Point2D) other;

        return other_.x == this.x && other_.z == this.z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + z;
        return result;
    }
}