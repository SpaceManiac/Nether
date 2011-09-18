package com.platymuus.bukkit.nether;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for Nether 2.0
 */
public class NetherPlugin extends JavaPlugin {
    
	private final NetherPlayerListener playerListener = new NetherPlayerListener(this);
    
    public static final int MODE_CLASSIC = 0;
    public static final int MODE_AGENT = 1;
    public static final int MODE_ADJUST = 2;

    public void onEnable() {
        getServer().getPluginManager().registerEvent(Type.PLAYER_PORTAL, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.High, this);
        
        System.out.println(this + " enabled");
    }
    
    public void onDisable() {
        System.out.println(this + " disabled");
    }
    
    public World getNormal() {
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment() == Environment.NORMAL) return world;
        }
        return null;
    }
    
}
