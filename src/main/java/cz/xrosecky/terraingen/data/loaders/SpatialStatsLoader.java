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

    }
}
