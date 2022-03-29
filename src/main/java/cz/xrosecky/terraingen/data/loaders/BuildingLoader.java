package cz.xrosecky.terraingen.data.loaders;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.Building;
import cz.xrosecky.terraingen.data.types.Point;
import cz.xrosecky.terraingen.data.types.RoofType;
import cz.xrosecky.terraingen.utils.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.postgis.MultiPolygon;
import org.postgis.PGgeometry;
import org.postgis.Polygon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class BuildingLoader extends AbstractLoader {

    public BuildingLoader(JavaPlugin plugin, DataStorage storage, Connection conn) {
        super(plugin, storage, conn);
    }

    @Override
    public void LoadRegion(Point2D from, Point2D to) {
        try {
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery(String.format("SELECT geom, bldgheight, eaveheight, baseheight, roofform FROM buildings WHERE geom && %s", DatabaseUtils.CornersToGeom(from, to)));

            while (r.next()) {
                MultiPolygon geom = (MultiPolygon) ((PGgeometry)r.getObject(1)).getGeometry();
                float height = r.getFloat(2);
                float roofHeight = r.getFloat(3);
                float alt = r.getFloat(4);

                RoofType roofType = RoofType.OTHER;
                switch(r.getString(5)) {
                    case "Flat":
                        roofType = RoofType.FLAT;
                        break;
                    case "Shed":
                        roofType = RoofType.SHED;
                        break;
                    case "Dome":
                        roofType = RoofType.DOME;
                        break;
                    case "Mansard":
                        roofType = RoofType.MANSARD;
                        break;
                    case "Gable":
                        roofType = RoofType.GABLE;
                        break;
                    case "Vault":
                        roofType = RoofType.VAULT;
                        break;
                    case "Spherical":
                        roofType = RoofType.SPHERICAL;
                        break;
                    case "Hip":
                        roofType = RoofType.HIP;
                        break;
                }

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
                            chunk.buildings.add(new Building(height, alt, roofHeight, roofType, geom));
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
