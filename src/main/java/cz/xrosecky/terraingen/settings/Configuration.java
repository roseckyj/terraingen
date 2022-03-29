package cz.xrosecky.terraingen.settings;

import com.google.gson.annotations.SerializedName;

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
}