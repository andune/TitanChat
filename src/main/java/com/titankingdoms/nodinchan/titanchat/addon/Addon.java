package com.titankingdoms.nodinchan.titanchat.addon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import com.nodinchan.ncloader.Loadable;
import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.command.Command;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Addon - Addon base
 * 
 * @author NodinChan
 *
 */
public class Addon extends Loadable implements Listener {
	
	protected final TitanChat plugin;
	
	private Addon instance;
	
	private final AddonManager manager;
	
	private Logger log = Logger.getLogger("TitanLog");
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	/**
	 * Addons for supporting other plugins
	 * 
	 * @param name The name of the Addon
	 */
	public Addon(String name) {
		super(name);
		this.plugin = TitanChat.getInstance();
		this.manager = plugin.getAddonManager();
	}
	
	/**
	 * Check if an Addon equals another
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof Addon)
			return ((Addon) object).getName().equals(getName());
		
		return false;
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
	 * Gets the data folder
	 * 
	 * @return The data folder
	 */
	public final File getDataFolder() {
		File dir = new File(manager.getAddonDir(), super.getName());
		dir.mkdir();
		return dir;
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
	
	/**
	 * Should be called in the constructor to initialise the instance
	 * 
	 * @param addon
	 */
	public final void init(Addon addon) {
		this.instance = addon;
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
	 * Registers the Listener
	 * 
	 * @param listener The Listener to register
	 */
	public final void register(Listener listener) {
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
	
	/**
	 * Reloads the config
	 */
	public final void reloadConfig() {
		if (configFile == null) { configFile = new File(new File(manager.getAddonDir(), super.getName()), "config.yml"); }
		
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
	
	/**
	 * Returns the Addon as a String
	 */
	@Override
	public String toString() {
		return "Addon:" + super.getName();
	}
}