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

public class Addon {
	
	protected TitanChat plugin;
	
	private Logger log = Logger.getLogger("TitanLog");
	
	private final String name;
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	public Addon(TitanChat plugin, String name) {
		this.plugin = plugin;
		this.name = name;
	}
	
	public void chatMade(String name, String message) {}
	
	public String format(Player player, String message) { return message; }
	
	public final FileConfiguration getConfig() {
		if (config == null) { reloadConfig(); }
		return config;
	}
	
	public final File getDataFolder() {
		File dir = new File(plugin.getAddonDir(), name);
		dir.mkdir();
		return dir;
	}
	
	public final Logger getLogger(String name) {
		if (log.equals(Logger.getLogger("TitanLog"))) { log = Logger.getLogger(name); }
		return log;
	}
	
	public final String getName() {
		return name;
	}
	
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
	
	public void init() {}
	
	public final void register(Addon addon) {
		plugin.getAddonManager().register(addon);
	}
	
	public final void register(CustomChannel channel) {
		plugin.getChannelManager().register(channel);
	}
	
	public void register(Command command) {
		plugin.getCommandManager().register(command);
	}
	
	public final void reloadConfig() {
		if (configFile == null) { configFile = new File(new File(plugin.getAddonDir(), name), "config.yml"); }
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = getResource("config.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	public final void saveConfig() {
		if (config == null || configFile == null) { return; }
		try { config.save(configFile); } catch (IOException e) {}
	}
}