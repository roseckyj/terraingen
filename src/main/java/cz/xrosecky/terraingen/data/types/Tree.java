package cz.xrosecky.terraingen.data.types;

public class Tree extends AbstractObject {
    public TreeType type;

    public long x;
    public long z;

    public Tree(long x, long z, TreeType type) {
        super(x, z);
        this.x = x;
        this.z = z;
        this.type = type;
    }
}