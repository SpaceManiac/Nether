package com.platymuus.bukkit.nether;

import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Main class for Nether 2.0
 */
public class NetherPlugin extends JavaPlugin {

    private final NetherPlayerListener playerListener = new NetherPlayerListener(this);

    public static final int MODE_CLASSIC = 0;

    public static final int MODE_AGENT = 1;

    public static final int MODE_ADJUST = 2;

    public static final String[] MODE_NAMES = { "Classic", "Agent", "Adjust" };

    public void onEnable() {
        // Write a default config if we need to
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        // Verify the mode is valid for the nether setting
        if (getServer().getAllowNether()) {
            if (getMode() == MODE_CLASSIC) {
                getConfig().set("mode", MODE_AGENT);
                getLogger().warning("Allow-nether is on, using Agent mode instead of Classic");
            }
        } else {
            if (getMode() != MODE_CLASSIC) {
                int prevMode = getMode();
                getConfig().set("mode", MODE_CLASSIC);
                getLogger().warning("Allow-nether is off, using Classic mode instead of " + MODE_NAMES[prevMode]);
            }
        }

        PluginManager pm = getServer().getPluginManager();
        if (getMode() == MODE_CLASSIC) {
            // In classic mode, use PLAYER_MOVE to detect portal entrance, and create our own world.
            new WorldCreator(getConfig().getString("worldName", "nether")).environment(Environment.NETHER).createWorld();
            pm.registerEvents(playerListener.new ClassicListener(), this);
        } else {
            // In AGENT and ADJUST mode, use the portal event
            pm.registerEvents(playerListener.new AdjustListener(), this);
        }

        String respawn = "off";
        if (getRespawn()) {
            respawn = "on";
            // If we should respawn players to the normal world, listen for respawn
            pm.registerEvents(playerListener.new RespawnListener(), this);
        }

        // Good morning
        getLogger().info("Enabled successfully. " + MODE_NAMES[getMode()] + " mode, respawn handling " + respawn);
    }

    // Helpers
    public World getNether() {
        return getServer().getWorld(getConfig().getString("worldName", "nether"));
    }

    public World getNormal() {
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment() != Environment.NETHER) {
                return world;
            }
        }
        return null;
    }

    public void logMessage(String message) {
        if (getConfig().getBoolean("log", false)) {
            getLogger().info(message);
        }
    }

    public TravelAgent adjustTravelAgent(TravelAgent agent, Player player) {
        if (getMode() == MODE_AGENT) {
            agent = new NetherTravelAgent(this, player.getName(), player.getWorld().getEnvironment());
        } else {
            boolean nether = player.getWorld().getEnvironment() == Environment.NETHER;
            logMessage(player.getName() + " is portalling to " + (nether ? "normal world" : "Nether"));
        }
        agent.setSearchRadius(getSearchRadius()).setCreationRadius(getCreationRadius()).setCanCreatePortal(getCanCreate());
        return agent;
    }

    // Config getting stuff
    public int getMode() {
        return getConfig().getInt("mode", MODE_AGENT);
    }

    public int getScale() {
        return getConfig().getInt("scale", 8);
    }

    public boolean getRespawn() {
        return getConfig().getBoolean("respawn", true);
    }

    public int getSearchRadius() {
        return getConfig().getInt("options.searchRadius", 24);
    }

    public int getCreationRadius() {
        return getConfig().getInt("options.creationRadius", 12);
    }

    public boolean getCanCreate() {
        return getConfig().getBoolean("options.canCreate", true);
    }

}
