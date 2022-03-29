package cz.xrosecky.terraingen.data.loaders;

import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Point2D;

public class DatabaseUtils {
    public static String CornersToGeom(Point2D from, Point2D to) {
        String[] corners = {
                Coords.XZToLatLon(from.x, from.z).toLatLonString(),
                Coords.XZToLatLon(to.x,   from.z).toLatLonString(),
                Coords.XZToLatLon(to.x,   to.z  ).toLatLonString(),
                Coords.XZToLatLon(from.x, to.z  ).toLatLonString()
        };
        String edges = String.join(", ", corners) + ", " + corners[0];

        return String.format("ST_GeomFromText('POLYGON((%s))')", edges);
    }
}
