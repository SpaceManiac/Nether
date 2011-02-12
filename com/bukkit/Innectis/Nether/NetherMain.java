package com.bukkit.Innectis.Nether;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;

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
	private final static Logger logger = Logger.getLogger("Minecraft");

	public NetherMain(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader)
	{
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	public void onEnable()
	{
		// Register events
		//PluginManager pm = getServer().getPluginManager();
		//pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.High, this);

		// Say hi
		PluginDescriptionFile pdfFile = this.getDescription();
		log(pdfFile.getName() + " v" + pdfFile.getVersion() + " enabled");
	}

	public void onDisable()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
		log(pdfFile.getName() + " v" + pdfFile.getVersion() + " disabled");
	}

	public void log(String text)
	{
		logger.log(Level.INFO, text);
	}

	public void logWarning(String text)
	{
		logger.log(Level.WARNING, text);
	}
}
