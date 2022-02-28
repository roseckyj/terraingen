package cz.xrosecky.terraingen.data.types;

public class Point extends AbstractObject {
    public long x;
    public long z;

    public Point(long x, long z) {
        super(x, z);
        this.x = x;
        this.z = z;
    }
}