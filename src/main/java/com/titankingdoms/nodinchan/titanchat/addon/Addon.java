package com.titankingdoms.nodinchan.titanchat.addon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.command.Command;

/**
 * Addon - Addon base
 * 
 * @author NodinChan
 *
 */
public class Addon {
	
	protected TitanChat plugin;
	
	private Logger log = Logger.getLogger("TitanLog");
	
	private final String name;
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	public Addon(String name) {
		this.plugin = TitanChat.getInstance();
		this.name = name;
	}
	
	/**
	 * Called after a chat message is sent
	 * 
	 * @param name The name of the Player
	 * 
	 * @param message The message sent
	 */
	public void chatMade(String name, String message) {}
	
	/**
	 * Called when a message is being formatted
	 * 
	 * @param player The message sender
	 * 
	 * @param message The message
	 * 
	 * @return The formatted message
	 */
	public String format(Player player, String message) { return message; }
	
	/**
	 * Gets the config
	 * 
	 * @return The config
	 */
	public final FileConfiguration getConfig() {
		if (config == null) { reloadConfig(); }
		return config;
	}
	
	/**
	 * Gets the data folder
	 * 
	 * @return The data folder
	 */
	public final File getDataFolder() {
		File dir = new File(plugin.getAddonDir(), name);
		dir.mkdir();
		return dir;
	}
	
	/**
	 * Gets the Logger
	 * 
	 * @param name The name of the Logger
	 * 
	 * @return The logger
	 */
	public final Logger getLogger(String name) {
		if (log.equals(Logger.getLogger("TitanLog"))) { log = Logger.getLogger(name); }
		return log;
	}
	
	/**
	 * Gets the name of the Addon
	 * 
	 * @return The name of the Addon
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Gets the file from the JAR file
	 * 
	 * @param fileName The name of the file
	 * 
	 * @return The file if found, otherwise null
	 */
	public final InputStream getResource(String fileName) {
		try {
			JarFile jarFile = new JarFile(plugin.getLoader().getPluginAddonJar(name));
			Enumeration<JarEntry> entries = jarFile.entries();
			
			while (entries.hasMoreElements()) {
				JarEntry element = entries.nextElement();
				
				if (element.getName().equalsIgnoreCase(fileName))
					return jarFile.getInputStream(element);
			}
			
		} catch (IOException e) {}
		
		return null;
	}
	
	/**
	 * Called when the Addon is loaded by the Loader
	 */
	public void init() {}
	
	/**
	 * Registers the Addon
	 * 
	 * @param addon The Addon to register
	 */
	public final void register(Addon addon) {
		plugin.getAddonManager().register(addon);
	}
	
	/**
	 * Registers the Command
	 * 
	 * @param command The Command to register
	 */
	public final void register(Command command) {
		plugin.getCommandManager().register(command);
	}
	
	/**
	 * Registers the Custom Channel
	 * 
	 * @param channel The Custom Channel to register
	 */
	public final void register(CustomChannel channel) {
		plugin.getChannelManager().register(channel);
	}
	
	/**
	 * Reloads the config
	 */
	public final void reloadConfig() {
		if (configFile == null) { configFile = new File(new File(plugin.getAddonDir(), name), "config.yml"); }
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = getResource("config.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	/**
	 * Saves the config
	 */
	public final void saveConfig() {
		if (config == null || configFile == null) { return; }
		try { config.save(configFile); } catch (IOException e) {}
	}
}