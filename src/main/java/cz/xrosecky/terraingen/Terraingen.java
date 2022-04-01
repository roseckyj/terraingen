package cz.xrosecky.terraingen;

import cz.xrosecky.terraingen.commands.GotoCommand;
import cz.xrosecky.terraingen.commands.WhereAmICommand;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.generator.ChunkGen;
import cz.xrosecky.terraingen.settings.ConfigManager;
import cz.xrosecky.terraingen.settings.Configuration;
import cz.xrosecky.terraingen.utils.StaticDB;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.SQLException;


@SuppressWarnings("unused")
public final class Terraingen extends JavaPlugin {
    private DataStorage storage;
    private java.sql.Connection conn;

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new ChunkGen(this, storage);
    }

    @Override
    public void onEnable() {
        ConfigManager configManager = new ConfigManager(this);

        try {
            this.getCommand("goto").setExecutor(new GotoCommand(this, storage));
            this.getCommand("whereami").setExecutor(new WhereAmICommand(this, storage));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Configuration config = configManager.getConfiguration();

        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://" + config.getDBHost() + ":" + config.getDBPort() + "/" + config.getDBDatabase();
            conn = DriverManager.getConnection(url, config.getDBUser(), config.getDBPassword());

            this.storage = new DataStorage(this, conn);

            getLogger().info("Successfully connected to the database");
        } catch( SQLException e ) {
            getLogger().severe("Connection to the database failed! Please check the credentials in configuration.json");
            setEnabled(false);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);

        try {
            if (conn != null) {
                conn.close();
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
