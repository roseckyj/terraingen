package cz.xrosecky.terraingen.data;

import cz.xrosecky.terraingen.utils.Point2D;

import java.util.HashMap;

public class DataStorage {
    private HashMap<Point2D, DataChunk> chunks = new HashMap<>();

    public DataChunk getChunk(int x, int z) {
        Point2D point = new Point2D(x, z);
        if (!chunks.containsKey(point)) {
            chunks.put(point, new DataChunk(x, z));
        }
        return chunks.get(point);
    }
}
