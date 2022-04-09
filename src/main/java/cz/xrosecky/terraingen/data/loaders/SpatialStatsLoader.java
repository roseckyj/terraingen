package cz.xrosecky.terraingen.data.loaders;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.Building;
import cz.xrosecky.terraingen.data.types.RoofType;
import cz.xrosecky.terraingen.data.types.SpatialData;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Point2D;
import cz.xrosecky.terraingen.utils.Pointf3D;
import org.bukkit.plugin.java.JavaPlugin;
import org.postgis.MultiPolygon;
import org.postgis.PGgeometry;
import org.postgis.Polygon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class SpatialStatsLoader extends AbstractLoader {

    public SpatialStatsLoader(JavaPlugin plugin, DataStorage storage, Connection conn) {
        super(plugin, storage, conn);
    }

    @Override
    public void LoadRegion(Point2D from, Point2D to) {
        try {
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery(String.format("SELECT geom, pocet_pm FROM brno_employment WHERE geom && %s", DatabaseUtils.CornersToGeom(from, to)));

            while (r.next()) {
                MultiPolygon geom = (MultiPolygon) ((PGgeometry)r.getObject(1)).getGeometry();
                int workspaces = r.getInt(2);

                int minX = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int minZ = Integer.MAX_VALUE;
                int maxZ = Integer.MIN_VALUE;

                for (int i = 0; i < geom.numPolygons(); i++) {
                    Polygon p = geom.getPolygon(i);
                    for (int j = 0; j < p.numPoints(); j++) {
                        Pointf3D pt = Coords.latLonToXZ(new Pointf3D(p.getPoint(j)));
                        minX = Math.min(minX, (int)Math.floor(pt.x));
                        maxX = Math.max(maxX, (int)Math.ceil(pt.x));
                        minZ = Math.min(minZ, (int)Math.floor(pt.z));
                        maxZ = Math.max(maxZ, (int)Math.ceil(pt.z));
                    }
                }

                minX = (int)Math.floor(minX / 16.0);
                maxX = (int)Math.ceil(maxX / 16.0);
                minZ = (int)Math.floor(minZ / 16.0);
                maxZ = (int)Math.ceil(maxZ / 16.0);

                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {

                        Point2D fetchPoint = new Point2D(x, z);
                        if (new Point2D(x * 16, z * 16).within(from, to)) {
                            DataChunk chunk = storage.chunks.get(fetchPoint);

                            if (!chunk.spatialStats.containsKey("workspaces")) {
                                chunk.spatialStats.put("workspaces", new ArrayList<SpatialData>());
                            }

                            chunk.spatialStats.get("workspaces").add(new SpatialData(geom, new HashMap<String, Integer>(){{
                                put("workspaces", workspaces);
                            }}));
                        }
                    }
                }
            }
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
