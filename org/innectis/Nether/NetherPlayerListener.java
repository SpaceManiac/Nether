package org.innectis.Nether;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;

public class NetherPlayerListener extends PlayerListener
{

	private NetherMain main;
	
	public NetherPlayerListener(NetherMain plugin)
	{
		main = plugin;
	}
	
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		// Return nether-deaths to normal world
		if (event.getRespawnLocation().getWorld().getEnvironment().equals(Environment.NETHER)) {
			// For now just head to the first world there.
			World normal = main.getServer().getWorlds().get(0);
			if (!normal.getEnvironment().equals(Environment.NORMAL)) {
				// Don't teleport to a non-normal world
				return;
			}
			
			Location respawnLocation = normal.getSpawnLocation();
			System.out.println(event.getPlayer().getName() + " respawns to normal world");
			event.setRespawnLocation(respawnLocation);
		}
	}

	
	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Location loc = event.getTo();
		World world = loc.getWorld();
		Block b = world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if (!b.getType().equals(Material.PORTAL)) {
			// Not a portal.
			return;
		}
		
		if (world.getEnvironment().equals(Environment.NORMAL)) {
			// First of all see if there IS a nether yet
			
			String netherName = main.getConfiguration().getString("nether-world-name");
			// Here we use "netherworld"
			if((netherName == null) || netherName.isEmpty()) netherName = "netherworld";
			
			World nether = main.getServer().getWorld(netherName);
			if (nether == null) {
				event.getPlayer().sendMessage(ChatColor.RED + "First load of world " + netherName + ", please wait.");
				nether = main.getServer().createWorld(netherName, Environment.NETHER);
			}
			
			if (!nether.getEnvironment().equals(Environment.NETHER)) {
				// Don't teleport to a non-nether world
				return;
			}
			
			
			// Try to find a portal near where the player should land
			Block dest = nether.getBlockAt(b.getX() / 8, b.getY(), b.getZ() / 8);
			NetherPortal portal = NetherPortal.findPortal(dest);
			Location spawn;
			if (portal == null) {
				portal = NetherPortal.createPortal(dest);
				spawn = portal.getSpawn();
				System.out.println(event.getPlayer().getName() + " portals to Nether [NEW]");
			} else {
				spawn = portal.getVerifiedSpawn();
				if(spawn == null){
					System.out.println(event.getPlayer().getName() + " failed to portal to Nether");
					spawn = NetherPortal.getVerifiedSpawn(b);
					if(spawn == null) spawn = b.getWorld().getSpawnLocation(); // Should never reach here.
				}
				else System.out.println(event.getPlayer().getName() + " portals to Nether");
			}
			
			// Go!
			event.getPlayer().teleportTo(spawn);
			event.setTo(spawn);
		} else if (world.getEnvironment().equals(Environment.NETHER)) {
			// For now just head to the first world there.
			World normal = main.getServer().getWorlds().get(0);
			
			if (!normal.getEnvironment().equals(Environment.NORMAL)) {
				// Don't teleport to a non-normal world
				return;
			}
			
			// Try to find a portal near where the player should land
			Block dest = normal.getBlockAt(b.getX() * 8, b.getY(), b.getZ() * 8);
			NetherPortal portal = NetherPortal.findPortal(dest);
			Location spawn;
			if (portal == null) {
				portal = NetherPortal.createPortal(dest);
				spawn = portal.getSpawn();
				System.out.println(event.getPlayer().getName() + " portals to normal world [NEW]");
			} else {
				spawn = portal.getVerifiedSpawn();
				if(spawn == null){
					System.out.println(event.getPlayer().getName() + " failed to portal to normal world");
					spawn = NetherPortal.getVerifiedSpawn(b);
					if(spawn == null) spawn = b.getWorld().getSpawnLocation(); // Should never reach here.
				}
				else System.out.println(event.getPlayer().getName() + " portals to normal world");
			}
			
			// Go!
			event.getPlayer().teleportTo(spawn);
			event.setTo(spawn);
		}
	}
	
}
