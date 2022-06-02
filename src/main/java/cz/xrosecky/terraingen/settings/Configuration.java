package cz.xrosecky.terraingen.settings;

import com.google.gson.annotations.SerializedName;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Configuration {

    @SerializedName("DBUser")
    private String DBUser = "postgres";

    @SerializedName("DBPassword")
    private String DBPassword = "Pa$$w0rd";

    @SerializedName("DBHost")
    private String DBHost = "localhost";

    @SerializedName("DBPort")
    private int DBPort = 5432;

    @SerializedName("DBDatabase")
    private String DBDatabase = "geodb";

    @SerializedName("World")
    private String World = "gen";



    public String getDBUser() {
        return this.DBUser;
    }

    public String getDBPassword() {
        return this.DBPassword;
    }

    public String getDBHost() {
        return this.DBHost;
    }

    public int getDBPort() {
        return this.DBPort;
    }

    public String getDBDatabase() {
        return this.DBDatabase;
    }

    public World getWorld(@NotNull JavaPlugin plugin) {
        List<World> worlds = plugin.getServer().getWorlds();

        for(World w : worlds){
            if (w.getName().equals(World)) {
                return w;
            }
        }

        plugin.getLogger().severe("World '" + World + "' does not exist on the server!");

        return null;
    }
}