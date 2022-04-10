package cz.xrosecky.terraingen.data.loaders;

import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.LineSegment;
import cz.xrosecky.terraingen.data.types.SegmentType;
import cz.xrosecky.terraingen.data.types.StreetSegment;
import cz.xrosecky.terraingen.data.types.StreetType;
import cz.xrosecky.terraingen.utils.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.postgis.LineString;
import org.postgis.MultiLineString;
import org.postgis.PGgeometry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class StreetLoader extends AbstractLoader {

    public StreetLoader(JavaPlugin plugin, DataStorage storage, Connection conn) {
        super(plugin, storage, conn);
    }

    @Override
    public void LoadRegion(Point2D from, Point2D to) {
        try {
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery(String.format("SELECT geom, code, name, bridge, tunnel FROM osm_roads WHERE geom && %s", DatabaseUtils.CornersToGeom(from, to)));

            while (r.next()) {
                MultiLineString geom = (MultiLineString) ((PGgeometry)r.getObject(1)).getGeometry();
                StreetType type = StreetType.fromCode(r.getInt(2));
                String name = r.getString(3);
                boolean bridge = r.getString(4).equals("T");
                boolean tunnel = r.getString(5).equals("T");

                for (int i = 0; i < geom.numLines(); i++) {
                    LineString l = geom.getLine(i);
                    for (int j = 0; j < l.numPoints() - 1; j++) {
                        Pointf3D a = Coords.latLonToXZ(new Pointf3D(l.getPoint(j)));
                        Pointf3D b = Coords.latLonToXZ(new Pointf3D(l.getPoint(j + 1)));

                        long x1 = Math.round(a.x);
                        long z1 = Math.round(a.z);
                        long x2 = Math.round(b.x);
                        long z2 = Math.round(b.z);

                        long minX = Math.min(x1, x2);
                        long maxX = Math.max(x1, x2);
                        long minZ = Math.min(z1, z2);
                        long maxZ = Math.max(z1, z2);

                        SegmentType segType = SegmentType.MIDDLE;
                        if (i == 0) {
                            segType = SegmentType.START;
                        }
                        if (i == geom.numLines() - 1) {
                            segType = SegmentType.END;
                        }

                        StreetSegment segment = new StreetSegment(x1, z1, x2, z2, type, name, bridge, tunnel, segType);

                        for (int x = (int) Math.floor(minX / 16.0); x <= (int) Math.ceil(maxX / 16.0); x++) {
                            for (int z = (int) Math.floor(minZ / 16.0); z <= (int) Math.ceil(maxZ / 16.0); z++) {

                                Point2D fetchPoint = new Point2D(x, z);
                                if (new Point2D(x * 16, z * 16).within(from, to)) {
                                    storage.chunks.get(fetchPoint).streets.add(segment);
                                }
                            }
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
