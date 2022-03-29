package cz.xrosecky.terraingen.data.loaders;

import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Point2D;
import cz.xrosecky.terraingen.utils.Pointf2D;
import cz.xrosecky.terraingen.utils.Pointf3D;
import org.bukkit.plugin.java.JavaPlugin;
import org.postgis.MultiPolygon;
import org.postgis.PGgeometry;
import org.postgis.Polygon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TerrainLoader extends AbstractLoader {

    public TerrainLoader(JavaPlugin plugin, DataStorage storage, Connection conn) {
        super(plugin, storage, conn);
    }

    @Override
    public void LoadRegion(Point2D from, Point2D to) {
        try {
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery(String.format("SELECT geom FROM terrain WHERE ST_Intersects(geom, %s)", DatabaseUtils.CornersToGeom(from, to)));

            while (r.next()) {
                MultiPolygon geom = (MultiPolygon) ((PGgeometry)r.getObject(1)).getGeometry();
                for (int polyNum = 0; polyNum < geom.numPolygons(); polyNum++) {
                    Polygon poly = geom.getPolygon(polyNum);
                    Pointf3D[] p = new Pointf3D[3];
                    for (int i = 0; i < 3; i++) {
                        p[i] = Coords.latLonToXZ(new Pointf3D(poly.getPoint(i)));
                    }

                    // Pineda algorithm - see https://is.muni.cz/auth/el/fi/jaro2021/PB009/um/slides/Lecture3_Rasterization_and_Filling.pdf
                    int minX = Integer.MAX_VALUE;
                    int maxX = Integer.MIN_VALUE;
                    int minZ = Integer.MAX_VALUE;
                    int maxZ = Integer.MIN_VALUE;

                    for (int j = 0; j < 3; j++) {
                        minX = Math.min(minX, (int) Math.floor(p[j].x));
                        maxX = Math.max(maxX, (int) Math.ceil(p[j].x));
                        minZ = Math.min(minZ, (int) Math.floor(p[j].z));
                        maxZ = Math.max(maxZ, (int) Math.ceil(p[j].z));
                    }

                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            double Eab = E(p[0].x, p[0].z, p[1].x, p[1].z, x, z);
                            double Ebc = E(p[1].x, p[1].z, p[2].x, p[2].z, x, z);
                            double Eca = E(p[2].x, p[2].z, p[0].x, p[0].z, x, z);
                            double Eabc = E(p[0].x, p[0].z, p[1].x, p[1].z, p[2].x, p[2].z);
                            if ((Eab <= 0 && Ebc <= 0 && Eca <= 0) || (Eab >= 0 && Ebc >= 0 && Eca >= 0)) {
                                // Inside triangle
                                Eab = Math.abs(Eab);
                                Ebc = Math.abs(Ebc);
                                Eca = Math.abs(Eca);
                                Eabc = Math.abs(Eabc);
                                double la = Ebc / Eabc;
                                double lb = Eca / Eabc;
                                double lc = Eab / Eabc;

                                int setX = (int) Math.floor(x / 16.0);
                                int setZ = (int) Math.floor(z / 16.0);

                                Point2D fetchPoint = new Point2D(setX, setZ);
                                if (new Point2D(x, z).within(from, to)) {
                                    storage.chunks.get(fetchPoint).setAlt(x, z, (int) Math.round(la * p[0].y + lb * p[1].y + lc * p[2].y));
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

    private double E(double Ax, double Ay, double Bx, double By, double Px, double Py) {
        return (By - Ay) * (Px - Ax) - (Bx - Ax) * (Py - Ay);
    }
}
