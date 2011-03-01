package org.innectis.Nether;

import java.util.ListIterator;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;

public class NetherPlayerListener extends PlayerListener {
	private static final int NETHER_COMPRESSION = 8;
	private static final boolean DEBUG = true;
	
	private NetherMain main;
	
	public NetherPlayerListener(NetherMain plugin) {
		main = plugin;
	}
	
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {		
		// Return nether-deaths to normal world
		if (event.getRespawnLocation().getWorld().getEnvironment().equals(Environment.NETHER)) {
			// For now just head to the first world there.
			World normal = main.getServer().getWorlds().get(0);
			if (!normal.getEnvironment().equals(Environment.NORMAL)) {
				// Don't teleport to a non-normal world
				return;
			}
			
			Location respawnLocation = normal.getSpawnLocation();
			System.out.println("NETHER_PLUGIN: " + event.getPlayer().getName() + " respawns to normal world");
			event.setRespawnLocation(respawnLocation);
		}
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		Location loc = event.getTo();
		World world = loc.getWorld();

		int locX = loc.getBlockX();
		int locY = loc.getBlockZ();
		int locZ = loc.getBlockY();

		Block b = world.getBlockAt(locX, locZ, locY);
		if (!b.getType().equals(Material.PORTAL)) {
			// Not a portal.
			return;
		}
		
		if (DEBUG)
			System.out.println("NETHER_PLUGIN: " + event.getPlayer().getName() + ": Hit portal at " + locX + ", "+ locY);
		
		// For better mapping between nether and normal, always use the lowest
		// xyz portal block
		while (world.getBlockAt(locX, locZ - 1, locY).getType().equals(Material.PORTAL))
			--locZ;
		while (world.getBlockAt(locX - 1, locZ, locY).getType().equals(Material.PORTAL))
			--locX;
		while (world.getBlockAt(locX, locZ, locY - 1).getType().equals(Material.PORTAL))
			--locY;
		
		if (DEBUG)
			System.out.println("NETHER_PLUGIN: " + event.getPlayer().getName() + ": Using portal block:" + locX + ", " + locY + ", " + locZ);
		
		// Now check to see which way the portal is oriented.
		boolean orientX = world.getBlockAt(locX + 1, locZ, locY).getType().equals(Material.PORTAL);
		
		if (DEBUG) {
			if (orientX)
				System.out.println("NETHER_PLUGIN: " + event.getPlayer().getName() + ": Portal is X oriented.");
			else
				System.out.println("NETHER_PLUGIN: " + event.getPlayer().getName() + ": Portal is Y oriented.");
		}

		if (world.getEnvironment().equals(Environment.NORMAL)) {
			// First of all see if there IS a nether yet
			// Here we use "netherworld"
			World nether = main.getServer().getWorld("netherworld");
			if (nether == null) {
				nether = main.getServer().createWorld("netherworld", Environment.NETHER);
			}
			
			if (!nether.getEnvironment().equals(Environment.NETHER)) {
				// Don't teleport to a non-nether world
				System.out.println("NETHER_PLUGIN: " + event.getPlayer().getName() + ": ERROR: Nether world not found, aborting transport.");
				return;
			}
			
			int signAdjX = 0;
			if (locX < 0)
				signAdjX = 1;
			int signAdjY = 0;
			if (locY < 0)
				signAdjY = 1;
			
			// Try to find a portal near where the player should land
			Block dest = nether.getBlockAt(((locX+signAdjX) / NETHER_COMPRESSION)-signAdjX, locZ, ((locY + signAdjY) / NETHER_COMPRESSION)-signAdjY);
			NetherPortal portal = NetherPortal.findPortal(dest, 1, event.getPlayer().getName());
			if (portal == null) {
				portal = NetherPortal.createPortal(dest, orientX);
			}
			
			// Go!
			Location spawn = portal.getSpawn();
			nether.loadChunk(spawn.getBlock().getChunk());
			event.getPlayer().teleportTo(spawn);
			event.setTo(spawn);
			
			event.setTo(spawn);
		} else if (world.getEnvironment().equals(Environment.NETHER)) {
			// For now just head to the first normal world there.
			ListIterator<World> worldIterator = main.getServer().getWorlds().listIterator();
			World normal = null;
			while (worldIterator.hasNext())
			{
				normal = worldIterator.next();
				if (normal.getEnvironment().equals(Environment.NORMAL))
					break;
				normal = null;	
			}
			
			if (null == normal) {
				// Don't teleport to a non-normal world
				System.out.println("NETHER_PLUGIN: " + event.getPlayer().getName() + ": ERROR: Normal world not found, aborting transport.");
				return;
			}

			// Try to find a portal near where the player should land
			Block dest = normal.getBlockAt(locX * NETHER_COMPRESSION, locZ, locY * NETHER_COMPRESSION);
			NetherPortal portal = NetherPortal.findPortal(dest, NETHER_COMPRESSION, event.getPlayer().getName());
			if (portal == null) {
				portal = NetherPortal.createPortal(dest, orientX);
			}
			
			// Go!
			Location spawn = portal.getSpawn();
			normal.loadChunk(spawn.getBlock().getChunk());
			event.getPlayer().teleportTo(spawn);
			event.setTo(spawn);
		}
	}
	
	public void ProcessMoveTo(Player player, Location location)	{
		if (location.getWorld().getEnvironment().equals(Environment.NETHER))
			System.out.println("NETHER_PLUGIN: " + player.getName() + ": Teleporting to NETHER!");
		else
			System.out.println("NETHER_PLUGIN: " + player.getName() + ": Teleporting to NORMAL WORLD!");
		
		player.teleportTo(location);
	}
}
