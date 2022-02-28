package cz.xrosecky.terraingen.data.types;

import cz.xrosecky.terraingen.utils.Point2D;

public abstract class AbstractObject {
    public double x;
    public double z;

    public AbstractObject(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public double distance(AbstractObject second) {
        return Math.sqrt(Math.pow(this.x - second.x, 2) + Math.pow(this.z - second.z, 2));
    }

    public double distance(Point2D second) {
        return Math.sqrt(Math.pow(this.x - second.x, 2) + Math.pow(this.z - second.z, 2));
    }
}
