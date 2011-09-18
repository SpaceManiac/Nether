package com.platymuus.bukkit.nether;

import org.bukkit.World;
import org.bukkit.event.player.*;

/**
 * Player listener for Nether 2.0
 */
class NetherPlayerListener extends PlayerListener {

    private NetherPlugin plugin;
    
    public NetherPlayerListener(NetherPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerPortal(PlayerPortalEvent event) {
        // Reduce search and creation radii
        event.getPortalTravelAgent().setCreationRadius(12).setSearchRadius(24);
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
		// Return nether-deaths to normal world
		if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
			World normal = plugin.getNormal();
            if (normal != null) {
                System.out.println(event.getPlayer().getName() + " respawned out of the Nether");
                event.setRespawnLocation(normal.getSpawnLocation());
            } else {
                System.out.println(event.getPlayer().getName() + " had no non-Nether to respawn to");
            }
		}
    }
    
}
