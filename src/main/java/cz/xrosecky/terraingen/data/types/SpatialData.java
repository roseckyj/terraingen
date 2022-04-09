package cz.xrosecky.terraingen.data.types;

import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Point2D;
import cz.xrosecky.terraingen.utils.Pointf3D;
import org.bukkit.Location;
import org.postgis.LinearRing;
import org.postgis.MultiPolygon;
import org.postgis.Polygon;

import java.util.HashMap;

public class SpatialData extends AbstractObject {
    public MultiPolygon polygon;
    public HashMap<String, Integer> data;
    public HashMap<Point2D, Boolean> cache = new HashMap<>();

    public SpatialData(MultiPolygon polygon, HashMap<String, Integer> data) {
        super(Coords.latLonToXZ(new Pointf3D(polygon.getPoint(0))).x, Coords.latLonToXZ(new Pointf3D(polygon.getPoint(0))).z);
        this.data = data;
        this.polygon = polygon;
    }

    public boolean isIn(int x, int z) {
        Point2D pt = new Point2D(x, z);
        if (cache.containsKey(pt)) {
            return cache.get(pt);
        }

        for (int polyNum = 0; polyNum < polygon.numPolygons(); polyNum++) {
            Polygon poly = polygon.getPolygon(polyNum);

            for (int ringNum = 0; ringNum < poly.numRings(); ringNum++) {
                LinearRing r = poly.getRing(ringNum);

                Pointf3D[] p = new Pointf3D[4];
                for (int i = 0; i < 4; i++) {
                    p[i] = Coords.latLonToXZ(new Pointf3D(r.getPoint(i)));
                }

                double Eab = E(p[0].x, p[0].z, p[1].x, p[1].z, x, z);
                double Ebc = E(p[1].x, p[1].z, p[2].x, p[2].z, x, z);
                double Ecd = E(p[2].x, p[2].z, p[3].x, p[3].z, x, z);
                double Eda = E(p[3].x, p[3].z, p[0].x, p[0].z, x, z);

                if ((Eab <= 0 && Ebc <= 0 && Ecd <= 0 && Eda <= 0) || (Eab >= 0 && Ebc >= 0 && Ecd >= 0 && Eda >= 0)) {
                    cache.put(pt, true);
                    return true;
                }
            }
        }
        cache.put(pt, false);
        return false;
    }

    private double E(double Ax, double Ay, double Bx, double By, double Px, double Py) {
        return (By - Ay) * (Px - Ax) - (Bx - Ax) * (Py - Ay);
    }
}