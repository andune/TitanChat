package com.titankingdoms.nodinchan.titanchat.channel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.addon.Addon;
import com.titankingdoms.nodinchan.titanchat.command.Command;

/**
 * CustomChannel - Custom Channel base
 * 
 * @author NodinChan
 *
 */
public class CustomChannel extends Channel implements Listener {

	protected final TitanChat plugin;
	
	private CustomChannel instance;
	
	private final ChannelManager manager;

	private Logger log = Logger.getLogger("TitanLog");
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	public CustomChannel(String name) {
		super(name, Type.CUSTOM);
		this.plugin = TitanChat.getInstance();
		this.manager = plugin.getChannelManager();
	}
	
	/**
	 * Colourise the line of text
	 * 
	 * @param text The text to be colourised
	 * 
	 * @return The colourised line of text
	 */
	public String colourise(String text) {
		return text.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	/**
	 * Decolourise the line of text
	 * 
	 * @param text The text to be decolourised
	 * 
	 * @return The decolourised line of text
	 */
	public String decolourise(String text) {
		return text.replaceAll("(&([a-f0-9A-F|kK]))", "");
	}
	
	/**
	 * Formats the message
	 * 
	 * @param sender The sender of the message
	 * 
	 * @param message The message to format
	 * 
	 * @return The formatted message
	 */
	public String format(Player sender, String format, String message) {
		return format.replace("%player", sender.getDisplayName()).replace("%message", message);
	}
	
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
	 * Gets the format of the channel
	 * 
	 * @return The format of the channel
	 */
	public String getFormat() {
		return "<%player> %message";
	}
	
	/**
	 * Gets the Logger
	 * 
	 * @return The Logger
	 */
	public Logger getLogger() {
		return log;
	}
	
	/**
	 * Gets the file from the JAR file
	 * 
	 * @param fileName The name of the file
	 * 
	 * @return The file if found, otherwise null
	 */
	public final InputStream getResource(String fileName) {
		return manager.getResource(instance, fileName);
	}
	
	public void init(CustomChannel channel) {
		this.instance = channel;
	}
	
	/**
	 * Registers the addon
	 * 
	 * @param addon the addon to register
	 */
	public final void register(Addon addon) {
		plugin.getAddonManager().register(addon);
	}
	
	/**
	 * Registers the command
	 * 
	 * @param command the command to register
	 */
	public final void register(Command command) {
		plugin.getCommandManager().register(command);
	}
	
	/**
	 * Registers the Listener
	 * 
	 * @param listener The Listener to register
	 */
	public final void register(Listener listener) {
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
	
	/**
	 * Called when TitanChat is reloaded
	 */
	public void reload() {}
	
	/**
	 * Reloads the config
	 */
	public final void reloadConfig() {
		if (configFile == null) { configFile = new File(new File(plugin.getAddonDir(), super.getName()), "config.yml"); }
		
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