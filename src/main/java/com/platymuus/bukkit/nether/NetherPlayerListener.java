package com.platymuus.bukkit.nether;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Player listener for Nether 2.0
 */
class NetherPlayerListener {

    private NetherPlugin plugin;

    public NetherPlayerListener(NetherPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Note: only registered when mode is CLASSIC

        Block b = event.getTo().getBlock();
        World world = b.getWorld();
        if (b.getType() != Material.PORTAL) {
            // Not a portal.
            return;
        }

        if (world.getEnvironment() == Environment.NORMAL) {
            // At this point the Nether has already been created.
            World nether = plugin.getNether();

            if (nether == null) {
                // Just in case.
                return;
            }

            // Try to find a portal near where the player should land
            Block dest = nether.getBlockAt(b.getX() / plugin.getScale(), b.getY(), b.getZ() / plugin.getScale());
            NetherPortal portal = NetherPortal.findPortal(dest, plugin.getSearchRadius());
            Location spawn;
            if (portal == null) {
                if (plugin.getCanCreate()) {
                    portal = NetherPortal.createPortal(dest);
                    spawn = portal.getSpawn();
                    plugin.logMessage(event.getPlayer().getName() + " portalled to Nether [NEW]");
                } else {
                    return;
                }
            } else {
                spawn = portal.getVerifiedSpawn();
                if (spawn == null) {
                    plugin.logMessage(event.getPlayer().getName() + " failed to portal to Nether");
                    spawn = NetherPortal.getVerifiedSpawn(b);
                    if (spawn == null) {
                        spawn = b.getWorld().getSpawnLocation(); // Should never reach here.
                    }
                } else {
                    plugin.logMessage(event.getPlayer().getName() + " portalled to Nether");
                }
            }

            // Go!
            event.getPlayer().teleport(spawn);
            event.setTo(spawn);
        } else if (world.getEnvironment() == Environment.NETHER) {
            // For now just head to the first world there.
            World normal = plugin.getNormal();

            if (normal == null) {
                // Don't teleport to a non-normal world
                return;
            }

            // Try to find a portal near where the player should land
            Block dest = normal.getBlockAt(b.getX() * plugin.getScale(), b.getY(), b.getZ() * plugin.getScale());
            NetherPortal portal = NetherPortal.findPortal(dest, plugin.getSearchRadius());
            Location spawn;
            if (portal == null) {
                if (plugin.getCanCreate()) {
                    portal = NetherPortal.createPortal(dest);
                    spawn = portal.getSpawn();
                    plugin.logMessage(event.getPlayer().getName() + " portals to normal world [NEW]");
                } else {
                    return;
                }
            } else {
                spawn = portal.getVerifiedSpawn();
                if (spawn == null) {
                    plugin.logMessage(event.getPlayer().getName() + " failed to portal to normal world");
                    spawn = NetherPortal.getVerifiedSpawn(b);
                    if (spawn == null) {
                        spawn = b.getWorld().getSpawnLocation(); // Should never reach here.
                    }
                } else {
                    plugin.logMessage(event.getPlayer().getName() + " portals to normal world");
                }
            }

            // Go!
            event.getPlayer().teleport(spawn);
            event.setTo(spawn);
        }
    }

    public class ClassicListener implements Listener {
        @EventHandler
        public void handle(PlayerMoveEvent event) {
            onPlayerMove(event);
        }
    }

    public class AdjustListener implements Listener {
        @EventHandler
        public void handle(PlayerPortalEvent event) {
            event.setPortalTravelAgent(plugin.adjustTravelAgent(event.getPortalTravelAgent(), event.getPlayer()));
        }
    }

    public class RespawnListener implements Listener {
        @EventHandler
        public void handle(PlayerRespawnEvent event) {
            // Note: only registered when respawn is true
            if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
                World normal = plugin.getNormal();
                if (normal != null) {
                    plugin.logMessage(event.getPlayer().getName() + " respawned out of the Nether");
                    event.setRespawnLocation(normal.getSpawnLocation());
                } else {
                    plugin.logMessage(event.getPlayer().getName() + " had no non-Nether to respawn to");
                }
            }
        }
    }

}
