package cz.xrosecky.terraingen.data.loaders;

import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.utils.Point2D;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public abstract class AbstractLoader {
    protected JavaPlugin plugin;
    protected DataStorage storage;
    protected Connection conn;

    public AbstractLoader(JavaPlugin plugin, DataStorage storage, Connection conn) {
        this.plugin = plugin;
        this.storage = storage;
        this.conn = conn;
    }

    public abstract void LoadRegion(Point2D from, Point2D to);
}
