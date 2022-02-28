package cz.xrosecky.terraingen.data.types;

public class LineSegment extends AbstractObject {
    public long x1;
    public long z1;
    public long x2;
    public long z2;

    public LineSegment(long x1, long z1, long x2, long z2) {
        super(x1, z1);
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
    }
}