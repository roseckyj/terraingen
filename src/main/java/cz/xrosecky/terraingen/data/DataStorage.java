package cz.xrosecky.terraingen.data;

import cz.xrosecky.terraingen.data.loaders.*;
import cz.xrosecky.terraingen.utils.Point2D;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;

public class DataStorage {
    public final HashMap<Point2D, DataChunk> chunks = new HashMap<>();
    private final HashSet<Point2D> fetching = new HashSet<>();
    private final AbstractLoader[] loaders;

    private final int FETCH_SIZE = 6;

    public DataStorage(JavaPlugin plugin, Connection conn) {
//        this.plugin = plugin;

        loaders = new AbstractLoader[] {
                new TerrainLoader(plugin, this, conn),
                new TreeLoader(plugin, this, conn),
                new LightLoader(plugin, this, conn),
                new StreetLoader(plugin, this, conn),
                new BuildingLoader(plugin, this, conn),
                new SpatialStatsLoader(plugin, this, conn)
        };
    }

    public DataChunk getChunk(int x, int z) {
        Point2D point = new Point2D(x, z);
        int xFetch = (int) Math.floor((float) x / FETCH_SIZE) * FETCH_SIZE;
        int zFetch = (int) Math.floor((float) z / FETCH_SIZE) * FETCH_SIZE;
        Point2D fetchPoint = new Point2D(xFetch, zFetch);
        while (fetching.contains(fetchPoint)) {
            try {
                Thread.sleep(10); // TODO: replace the busy waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!chunks.containsKey(point)) {
            fetching.add(fetchPoint);

            for (int xD = 0; xD < FETCH_SIZE; xD++) {
                for (int zD = 0; zD < FETCH_SIZE; zD++) {
                    Point2D pointToInsert = new Point2D(xFetch + xD, zFetch + zD);
                    chunks.put(pointToInsert, new DataChunk(pointToInsert.x, pointToInsert.z));
                }
            }
            fetchChunk(xFetch, zFetch);
            //plugin.getLogger().info("Fetching data chunk " + xFetch + " " + zFetch + " (requested by " + x + " " + z + ")");
            fetching.remove(fetchPoint);
        }
        return chunks.get(point);
    }

    private void fetchChunk(int chunkX, int chunkZ) {
        Point2D from = new Point2D(chunkX * 16, chunkZ * 16);
        Point2D to = new Point2D((chunkX + FETCH_SIZE) * 16, (chunkZ + FETCH_SIZE) * 16);
        for (AbstractLoader l : loaders) {
            l.LoadRegion(from, to);
        }
    }
}
