package cz.xrosecky.terraingen.data.types;

public class StreetSegment extends LineSegment {
    public final StreetType type;
    public final boolean bridge;
    public final boolean tunnel;
    public final String name;

    public StreetSegment(long x1, long z1, long x2, long z2, StreetType type, String name, boolean bridge, boolean tunnel) {
        super(x1, z1, x2, z2);
        this.type = type;
        this.name = name;
        this.bridge = bridge;
        this.tunnel = tunnel;
    }
}
