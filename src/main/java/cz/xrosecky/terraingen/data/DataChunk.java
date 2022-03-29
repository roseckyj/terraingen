package cz.xrosecky.terraingen.data;

import cz.xrosecky.terraingen.data.types.Building;
import cz.xrosecky.terraingen.data.types.LineSegment;
import cz.xrosecky.terraingen.data.types.Point;
import cz.xrosecky.terraingen.data.types.Tree;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Point2D;

import java.util.ArrayList;
import java.util.HashMap;

public class DataChunk {
    public final int x;
    public final int z;

    public final ArrayList<Tree> trees = new ArrayList<>();
    public final ArrayList<Point> lights = new ArrayList<>();
    public final ArrayList<LineSegment> streets = new ArrayList<>();
    public final ArrayList<Building> buildings = new ArrayList<>();
    private final int[][] altitudes = new int[16][16];

    public DataChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public void setAlt(int x, int z, int alt) {
        altitudes[((x % 16) + 16) % 16][((z % 16) + 16) % 16] = alt;
    }

    public int getAlt(int x, int z) {
        return altitudes[((x % 16) + 16) % 16][((z % 16) + 16) % 16];
    }

    public boolean isInChunk(int absX, int absZ) {
        return isInChunk((long)absX, (long)absZ);
    }
    public boolean isInChunk(long absX, long absZ) {
        return absX >= (long)x * 16 && absX < ((long)x + 1) * 16 && absZ >= (long)z * 16 && absZ < ((long)z + 1) * 16;
    }

}
