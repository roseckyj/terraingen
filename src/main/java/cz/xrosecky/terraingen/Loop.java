package cz.xrosecky.terraingen;

import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.SpatialData;
import cz.xrosecky.terraingen.utils.Point2D;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Set;

public class Loop implements Runnable {
    private JavaPlugin plugin;
    private DataStorage storage;

    public Loop(JavaPlugin plugin, DataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    private ArrayList<BossBar> bars = new ArrayList<>();

    @Override
    public void run() {
        for (BossBar b : bars) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hideBossBar(b);
            }
        }
        bars = new ArrayList<BossBar>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location l = p.getLocation();
            SpatialData d = storage.getChunk((int)Math.floor(l.getX() / 16.0), (int)Math.floor(l.getZ() / 16.0)).getData("workspaces", (int)l.getX(), (int)l.getZ());

            if (d != null) {
                final Component name = Component.text(d.data.get("workspaces") + " volných pracovních míst v této oblasti");
                final BossBar bossBar = BossBar.bossBar(name, 1, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);

                p.showBossBar(bossBar);
                bars.add(bossBar);
            }
        }
    }
}