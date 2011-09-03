package com.platymuus.bukkit.nether;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

public class NetherPlugin extends JavaPlugin {
    
	private final NetherPlayerListener playerListener = new NetherPlayerListener(this);

    public void onEnable() {
        getServer().getPluginManager().registerEvent(Type.PLAYER_PORTAL, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Low, this);
        
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
