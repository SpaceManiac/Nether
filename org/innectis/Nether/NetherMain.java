package org.innectis.Nether;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Innectis Nether plugin class
 *
 * Features:
 *   Single-player style Nether portals
 *
 * @author Innectis
 */
public class NetherMain extends JavaPlugin
{
	private final NetherPlayerListener playerListener = new NetherPlayerListener(this);	
	
	public void onEnable() {		
		// Register events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.High, this);

		// Say hi
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " v" + pdfFile.getVersion() + " enabled");
	}

	public void onDisable() {
	}
}
