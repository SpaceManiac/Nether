package org.innectis.Nether;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;

public class NetherPortal {
	private final static boolean DEBUG = false;
	
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

	// Output a debug representation of the search for a portal block
	public static void logSearch(char[][] a, int searchDistance) {
		if (DEBUG) {
			for (int y = searchDistance - 1; y >= 0; --y) {
				String line = "";
				for (int x = 0; x < searchDistance; ++x) {
					if (a[x][y] != ' ')
						line += a[x][y];
					else
						line += '.';
				}
				System.out.println(line);
			}
		}
	}
	
	// ==============================
	// Check a column for portal blocks, starting with the players location
	public static NetherPortal checkCol(World world, int x, int y, int z) {
		// Portals are 3 blocks tall, so we only need to check every few blocks.
		// Start at the user's height and go outward.
		int d = z - 1;
		int u = z + 1;
		Block b = null;

		while (d >= 0 || u <= 127) {
			if (d >= 0)	{
				b = world.getBlockAt(x, d, y);
				if (b.getType().equals(Material.PORTAL)) {
					return new NetherPortal(b);
				}
				d -= 3;
			}
			if (u <= 127) {
				b = world.getBlockAt(x, u, y);
				if (b.getType().equals(Material.PORTAL)) {
					return new NetherPortal(b);
				}
				u += 3;
			}
		}

		return null;
	}

	// ==============================
	// Check for nearby portal within specified search distance
	// Should return nearest first.
	public static NetherPortal findPortal(Block dest, int searchDistance) {
		World world = dest.getWorld();
		
		int startX = dest.getX();		
		int startY = dest.getZ();		
		int startZ = dest.getY();

		int x = (searchDistance / 2), y = (searchDistance / 2);
		
		// Check middle block first
		NetherPortal np = checkCol(world, startX + x, startY + y, startZ);

		// Going IN to the nether, the search distance should be 1, and if
		// there's already a portal,
		// it will occupy this block.
		if (searchDistance < 2 || null != np)
			return np;

		// Since a portal is 2 blocks wide, we only need to
		// check every other column.  We'll flip this flag
		// after each check.
		boolean checkColumn = false;
		
		// Start in the middle and loop outward.
		//
		//  8
		// [^][6][>][>][>][>][>][6]
		// [^][^][4][>][>][>][4][v]
		// [^][^][^][2][>][2][v][v]
		// [^][^][^][^][0][v][v][v]
		// [^][^][^][1][1][v][v][v]
		// [^][^][3][<][<][3][v][v]
		// [^][5][<][<][<][<][5][v]
		// [7][<][<][<][<][<][<][7]

		char[][] c;
		if (DEBUG) {
			c = new char[searchDistance][searchDistance];
			c[x][y] = 'S';
			
			System.out.println("Starting portal search at (" + (x + startX) + ", " + (y + startY) + ").");
		}

		int sign = -1;
		for (int n = 1; n <= searchDistance; ++n) {
			// go in [sign] direction along the y axis [n] times
			// go in [sign] direction along the x axis [n] times
			// reverse [sign] and increment [n]
			for (int xy = 0; xy < 2; ++xy) {
				for (int i = 1; i <= n; ++i) {
					if (0 == xy)
						y += sign;
					else
						x += sign;

					if (checkColumn)
						np = checkCol(world, x + startX, y + startY, startZ);
					
					
					if (null != np) {
						if (DEBUG) {
							c[x][y] = 'X';
							logSearch(c, searchDistance);
						}
						return np;
					}

					if (DEBUG) {
						if (checkColumn) {							
							if (0 == xy) {
								if (sign < 0)
									c[x][y] = 'v';
								else
									c[x][y] = '^';
							} else {
								if (sign < 0)
									c[x][y] = '<';
								else
									c[x][y] = '>';
							}
						}
						else
							c[x][y] = ' ';
					}
					
					// I flip my bits back and forth, I flip my bits back and forth, I flip
					// my bits back and forth, I flip my bits back and forth, I flip my bits
					// back and forth
					checkColumn = !checkColumn;
		
					// Because we start going down, left, up, right, we'll
					// always end traveling along the y axis
					// on the first iteration where n == searchDistance and
					// we'll only need to travel n-1 blocks
					if (0 == xy && n == searchDistance && i + 1 == n) {
						if (DEBUG)
							logSearch(c, searchDistance);

						// Didn't find a portal
						return null;
					}
				}
			}

			sign *= -1;
		}
		
		if (DEBUG)
			logSearch(c, searchDistance);

		// Didn't find a portal
		return null;
	}
	
	// Create a new portal at the specified block, fudging position if needed
	// Will occasionally end up making portals in bad places, but let's hope not
	public static NetherPortal createPortal(Block dest, boolean orientX) {
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
		int x = dest.getX(), y = dest.getY(), z = dest.getZ();
		
		// Clear area around portal
		ArrayList<Block> columns = new ArrayList<Block>();
		for (int x2 = x - 4; x2 <= x + 5; ++x2) {
			for (int z2 = z - 4; z2 <= z + 5; ++z2) {
				int dx = x - x2, dz = z - z2;
				if (dx * dx + dz * dz < 12) {
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
		
		// Build obsidian frame
		for (int xd = -1; xd < 3; ++xd) {
			for (int yd = -1; yd < 4; ++yd) {
				if (xd == -1 || yd == -1 || xd == 2 || yd == 3) {
					Block b = null;
					if (orientX)
						b = world.getBlockAt(x + xd, y + yd, z);
					else
						b = world.getBlockAt(x, y + yd, z + xd);

					b.setType(Material.OBSIDIAN);
				}
			}
		}
		
		// Set it alight!
		dest.setType(Material.FIRE);
		
		return new NetherPortal(dest);
	}	
}
