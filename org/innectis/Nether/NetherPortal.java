package org.innectis.Nether;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;

import java.util.ArrayList;
import java.util.List;

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
		if (block.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ()).getType().equals(Material.PORTAL) ||
				block.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ()).getType().equals(Material.PORTAL)) {
			// portal is in X direction
			return new Location(block.getWorld(), block.getX() + 0.5,
					block.getY(), block.getZ() + 1.5 - 2 * Math.round(Math.random()));
		} else {
			// portal is in Z direction
			return new Location(block.getWorld(), block.getX() + 1.5 - 2 * Math.round(Math.random()),
					block.getY(), block.getZ() + 0.5);
		}
	}
	
	
	// If this is not a new Nether portal, it is best to check for room to spawn.
	public Location getVerifiedSpawn() {
		return getVerifiedSpawn(block);
	}
	
	
	// Check at an arbitrary location, used for failed teleport, to move back out of the portal frame.
	public static Location getVerifiedSpawn(Block checkBlock) {
		List<Block> list = listSpawns(checkBlock, true, 0);
		
		if (list != null && !list.isEmpty()) {
			// Use a random verified location to spawn.
			Block targetBlock = list.get((int) (Math.random() * list.size()));
			return new Location(checkBlock.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY() - 0.75, targetBlock.getZ() + 0.5);
		}
		
		// Otherwise spawn inside portal, for now.
		// return new Location(checkBlock.getWorld(), block.getX() + 0.5, block.getY() - 0.75, block.getZ() + 0.5);
		// No space found, return in error.
		return null;
	}
	
	
	// Lists the valid spawns near a portal, you should pass this method one of the two top portal blocks.
	// Checks 4 spawns per portal block, for a total of 24.
	private static List<Block> listSpawns(Block theBlock, boolean iteratePortal, int direction) {
		Block checkBlock;
		final BlockFace[] faces = {BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		List<Block> list = new ArrayList<Block>();
		
		// Scan for an air pocket beside portal blocks.
		for (int x = 0; x < faces.length; ++x) {
			checkBlock = theBlock.getRelative(faces[x]);
			
			if (checkBlock.getType() == Material.PORTAL) {
				if (iteratePortal) {
					// If it is another portal block, check beside it too.  And below: (faces[x] == BlockFace.DOWN)
					if (x == 0) list.addAll(listSpawns(checkBlock, true, x));
					else list.addAll(listSpawns(checkBlock, false, x));
				}
			} else if (checkBlock.getType() != Material.OBSIDIAN) {
				// Check for air directly beside portal.
				if (canBreathe(checkBlock.getTypeId()) && canBreathe(checkBlock.getRelative(BlockFace.DOWN).getTypeId())) {
					list.add(checkBlock);
				}
				
				// Also check the next block out too.
				checkBlock = checkBlock.getRelative(faces[x]);
				if (canBreathe(checkBlock.getTypeId()) && canBreathe(checkBlock.getRelative(BlockFace.DOWN).getTypeId())) {
					list.add(checkBlock);
				}
			}
		}
		
		return list;
	}
	
	
	// ==============================
	// Find a nearby portal within 16 blocks of the given block
	// Not guaranteed to be the nearest
	public static NetherPortal findPortal(Block dest) {
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
	public static NetherPortal createPortal(Block dest) {
		World world = dest.getWorld();
		
		// Not too high or too low overall
		if (dest.getY() > 120) {
			dest = world.getBlockAt(dest.getX(), 120, dest.getZ());
		} else if (dest.getY() < 8) {
			dest = world.getBlockAt(dest.getX(), 8, dest.getZ());
		}
		
		
		// Search for an area along the y axis that is suitable.
		// Will check nearest blocks to dest first.
		
		Block checkBlock, chosenBlock = dest;
		int quality, chosenQuality = 0;
		
		for (int y1 = dest.getY(), y2 = dest.getY(); (y1 > 4) || (y2 <= 124); --y1, ++y2) {
			// Look below.
			if (y1 > 4) {
				checkBlock = world.getBlockAt(dest.getX(), y1, dest.getZ());
				quality = checkPortalQuality(checkBlock);
				
				if (quality > chosenQuality) {
					chosenQuality = quality;
					chosenBlock = checkBlock;
					
					if (quality >= 28) break;
				}
			}
			
			// Look above.
			if (y2 <= 124 && y2 != y1) {
				checkBlock = world.getBlockAt(dest.getX(), y2, dest.getZ());
				quality = checkPortalQuality(checkBlock);
				
				if (quality > chosenQuality) {
					chosenQuality = quality;
					chosenBlock = checkBlock;
					
					if (quality >= 28) break;
				}
			}
		}
		
		dest = chosenBlock;
		
		// Create the physical portal
		// For now, don't worry about direction
		
		int x = dest.getX(), y = dest.getY(), z = dest.getZ();
		
		// Clear area around portal
		ArrayList<Block> columns = new ArrayList<Block>();
		for (int x2 = x - 4; x2 <= x + 4; ++x2) {
			for (int z2 = z - 4; z2 <= z + 4; ++z2) {
				float dx = (x - x2)+0.5f, dz = z - z2;
				if (dx * dx + dz * dz < 9) {
					columns.add(world.getBlockAt(x2, 0, z2));
				}
			}
		}
		
		// Clear area around portal
		for (Block col : columns) {
			// Stone platform, if needed.
			checkBlock = world.getBlockAt(col.getX(), y - 1, col.getZ());
			if (!canStand(checkBlock.getTypeId())) {
				if (world.getEnvironment() == Environment.NETHER) checkBlock.setType(Material.NETHERRACK);
				else checkBlock.setType(Material.STONE);
			}
			
			// Air pocket.
			for (int yd = 0; yd < 3; ++yd) {
				checkBlock = world.getBlockAt(col.getX(), y + yd, col.getZ());
				if (!canBreathe(checkBlock.getTypeId())) checkBlock.setType(Material.AIR);
			}
			
			// Roof, if needed.
			checkBlock = world.getBlockAt(col.getX(), y + 3, col.getZ());
			if (canFall(checkBlock.getTypeId())) {
				if (world.getEnvironment() == Environment.NETHER) checkBlock.setType(Material.NETHERRACK);
				else checkBlock.setType(Material.STONE);
			}
		}
		
		// Build obsidian frame
		for (int xd = -1; xd < 3; ++xd) {
			for (int yd = -1; yd < 4; ++yd) {
				if (xd == -1 || yd == -1 || xd == 2 || yd == 3) {
					world.getBlockAt(x + xd, y + yd, z).setType(Material.OBSIDIAN);
				}
				
				// Be sure the portal is full of only air, at this point.
				if ((xd == 0 || xd == 1) && yd > -1 && yd < 3) {
					world.getBlockAt(x + xd, y + yd, z).setType(Material.AIR);
				}
			}
		}
		
		// Set it alight!
		dest.setType(Material.FIRE);
		
		return new NetherPortal(dest);
	}
	
	
	// This depends on portals being made +1 in the X direction, as they currently are.
	// Blocks are cached in CraftBukkit for speed.  Returns a value 0-28.
	private static int checkPortalQuality(Block checkBlock) {
		int quality = 0;
		
		// Check inside frame. Priority high-low, total 18.
		if (canBreathe(checkBlock.getTypeId())) quality += 6;
		if (canBreathe(checkBlock.getRelative(1, 0, 0).getTypeId())) quality += 6;
		if (canBreathe(checkBlock.getRelative(0, 1, 0).getTypeId())) quality += 2;
		if (canBreathe(checkBlock.getRelative(1, 1, 0).getTypeId())) quality += 2;
		if (canBreathe(checkBlock.getRelative(0, 2, 0).getTypeId())) quality += 1;
		if (canBreathe(checkBlock.getRelative(1, 2, 0).getTypeId())) quality += 1;
		
		// Check ground under frame.  Priority mid, total 6.
		if (canStand(checkBlock.getRelative(0, -1, 0).getTypeId())) quality += 3;
		if (canStand(checkBlock.getRelative(1, -1, 0).getTypeId())) quality += 3;
		
		// Check ground around frame.  Priority low, total 4.
		if (canStand(checkBlock.getRelative(0, -1, -1).getTypeId())) quality += 1;
		if (canStand(checkBlock.getRelative(1, -1, -1).getTypeId())) quality += 1;
		if (canStand(checkBlock.getRelative(0, -1, 1).getTypeId())) quality += 1;
		if (canStand(checkBlock.getRelative(1, -1, 1).getTypeId())) quality += 1;
		
		return quality;
	}
	
	
	private static boolean canStand(int mat) {
		// Leave out the types that have to be atop a solid block, though not plants,
		// and others you don't want to destroy too: 55,63,64,65,66,68,69,70,71,72,75,76,77,93,94
		final int[] notSupporting = {0,6,8,9,10,11,37,38,39,40,50,51,59,83,85,90};
		
		for (int x = 0; x < notSupporting.length; ++x) {
			if (mat == notSupporting[x]) return false;
		}
		
		return true;
	}
	
	
	private static boolean canBreathe(int mat) {
		// All the types that include a breathable air pocket, not including fire.
		final int[] isBreathable = {0,6,37,38,39,40,50,55,59,63,64,65,66,68,69,70,71,72,75,76,77,83,93,94};
		
		for (int x = 0; x < isBreathable.length; ++x) {
			if (mat == isBreathable[x]) return true;
		}
		
		return false;
	}
	
	
	private static boolean canFall(int mat) {
		// All the types that can fall on the player, causing much pain.
		final int[] isUnstable = {8,9,10,11,12,13};
		
		for (int x = 0; x < isUnstable.length; ++x) {
			if (mat == isUnstable[x]) return true;
		}
		
		return false;
	}
}
