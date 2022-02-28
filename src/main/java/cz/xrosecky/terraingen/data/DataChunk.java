package cz.xrosecky.terraingen.data;

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

    public ArrayList<Tree> trees = new ArrayList<>();
    public ArrayList<Point> lights = new ArrayList<>();
    public ArrayList<LineSegment> streets = new ArrayList<>();
    private int[][] altitudes = new int[16][16];

    public DataChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public void setAlt(int x, int z, double alt) {
        altitudes[((x % 16) + 16) % 16][((z % 16) + 16) % 16] = Coords.normalizeY(alt);
    }

    public int getAlt(int x, int z) {
        return altitudes[((x % 16) + 16) % 16][((z % 16) + 16) % 16];
    }

    public boolean isInChunk(int absX, int absZ) {
        return absX >= x * 16 && absX < (x + 1) * 16 && absZ >= z * 16 && absZ < (z + 1) * 16;
    }
    public boolean isInChunk(long absX, long absZ) {
        return absX >= (long)x * 16 && absX < ((long)x + 1) * 16 && absZ >= (long)z * 16 && absZ < ((long)z + 1) * 16;
    }

}
