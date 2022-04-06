package cz.xrosecky.terraingen.data.loaders;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.Point;
import cz.xrosecky.terraingen.data.types.Tree;
import cz.xrosecky.terraingen.data.types.TreeType;
import cz.xrosecky.terraingen.utils.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.postgis.MultiPoint;
import org.postgis.PGgeometry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class LightLoader extends AbstractLoader {

    public LightLoader(JavaPlugin plugin, DataStorage storage, Connection conn) {
        super(plugin, storage, conn);
    }

    @Override
    public void LoadRegion(Point2D from, Point2D to) {
        try {
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery(String.format("SELECT geom, typ_sv__mi FROM brno_lights WHERE geom && %s", DatabaseUtils.CornersToGeom(from, to)));

            while (r.next()) {
                String lightType = StringUtils.unaccent(r.getString(2)).toLowerCase(Locale.ROOT);
                if (!lightType.equals("stozar")) {
                    continue;
                }

                org.postgis.Point geom = (org.postgis.Point) ((PGgeometry)r.getObject(1)).getGeometry();

                Pointf3D point = Coords.latLonToXZ(new Pointf3D(geom));

                int setX = (int) Math.floor(Math.round(point.x) / 16.0);
                int setZ = (int) Math.floor(Math.round(point.z) / 16.0);

                Point2D fetchPoint = new Point2D(setX, setZ);
                if (new Point2D((int)Math.round(point.x), (int)Math.round(point.z)).within(from, to)) {
                    DataChunk chunk = storage.chunks.get(fetchPoint);
                    chunk.lights.add(new Point(Math.round(point.x), Math.round(point.z)));
                }
            }
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
