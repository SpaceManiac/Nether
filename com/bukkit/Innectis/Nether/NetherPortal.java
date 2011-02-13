package com.bukkit.Innectis.Nether;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;

public class NetherPortal {
	
	private Block block;
	
	public NetherPortal(Block b) {
		block = b;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public void setBlock(Block b) {
		block = b;
	}
	
	// Return a random spawnable location
	public Location getSpawn() {
		double radius = 3 + 2 * Math.random();
		double angle = 2 * Math.PI * Math.random();
		double dx = radius * Math.cos(angle);
		double dz = radius * Math.sin(angle);
		return new Location(block.getWorld(), block.getX() + 0.5 + dx, block.getY(), block.getZ() + 0.5 + dz);
	}
	
	// ==============================
	// Find a nearby portal within 16 blocks of the given block
	// Not guaranteed to be the nearest
	public static NetherPortal findPortal(Block dest)
	{
		World world = dest.getWorld();
		
		// Get list of columns in a circle around the block
		ArrayList<Block> columns = new ArrayList<Block>();
		for (int x = dest.getX() - 16; x <= dest.getX() + 16; ++x) {
			for (int z = dest.getZ() - 16; z <= dest.getZ() + 16; ++z) {
				int dx = dest.getX() - x, dz = dest.getZ() - z;
				if (dx * dx + dz * dz <= 256) {
					columns.add(world.getBlockAt(x, 0, z));
				}
			}
		}
		
		// For each column try to find a portal block
		for (Block col : columns) {
			for (int y = 127; y >= 0; --y) {
				Block b = world.getBlockAt(col.getX(), y, col.getZ());
				if (b.getType().equals(Material.PORTAL)) {
					// Huzzah!
					return new NetherPortal(b);
				}
			}
		}
		
		// Nope!
		return null;
	}
	
	// Create a new portal at the specified block, fudging position if needed
	// Will occasionally end up making portals in bad places, but let's hope not
	public static NetherPortal createPortal(Block dest)
	{
		World world = dest.getWorld();
		
		// Try not to spawn within water or lava
		Material m = dest.getType();
		while ((m.equals(Material.LAVA) || m.equals(Material.WATER) ||
				m.equals(Material.STATIONARY_LAVA) || m.equals(Material.STATIONARY_WATER)) &&
				dest.getY() < 70) {
			dest = world.getBlockAt(dest.getX(), dest.getY() + 4, dest.getZ());
			m = dest.getType();
		}
		
		// Not too high or too low overall
		if (dest.getY() > 120) {
			dest = world.getBlockAt(dest.getX(), 120, dest.getZ());
		} else if (dest.getY() < 8) {
			dest = world.getBlockAt(dest.getX(), 8, dest.getZ());
		}
		
		// Create the physical portal
		// For now, don't worry about direction
		
		int x = dest.getX(), y = dest.getY(), z = dest.getZ();
		
		// Clear area around portal
		ArrayList<Block> columns = new ArrayList<Block>();
		for (int x2 = x - 6; x2 <= x + 6; ++x2) {
			for (int z2 = z - 6; z2 <= z + 6; ++z2) {
				int dx = x - x2, dz = z - z2;
				if (dx * dx + dz * dz <= 36) {
					columns.add(world.getBlockAt(x2, 0, z2));
				}
			}
		}
		
		// Clear area around portal
		for (Block col : columns) {
			// Stone platform
			world.getBlockAt(col.getX(), y - 1, col.getZ()).setType(Material.STONE);
			for (int yd = 0; yd < 4; ++yd) {
				world.getBlockAt(col.getX(), y + yd, col.getZ()).setType(Material.AIR);
			}
		}
		
		// Portal itself
		for (int xd = -1; xd < 3; ++xd) {
			for (int yd = -1; yd < 4; ++yd) {
				Material place = Material.PORTAL;
				
				// set borders to obsidian
				if (xd == -1 || yd == -1 || xd == 2 || yd == 3) place = Material.OBSIDIAN;
				
				world.getBlockAt(x + xd, y + yd, z).setType(place);
			}
		}
		
		return new NetherPortal(dest);
	}
	
}
