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
import com.titankingdoms.nodinchan.titanchat.command.Command;

public class Addon {
	
	protected static TitanChat plugin;
	
	private static Logger log = Logger.getLogger("TitanLog");
	
	private static String name;
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	public Addon(TitanChat plugin, String name) {
		Addon.plugin = plugin;
		Addon.name = name;
	}
	
	public void chatMade(String name, String message) {}
	
	public String chatMade(Player player, String message) { return message; }
	
	public FileConfiguration getConfig() {
		if (config == null) { reloadConfig(); }
		return config;
	}
	
	public File getDataFolder() {
		File dir = new File(plugin.getAddonDir(), name);
		dir.mkdir();
		return dir;
	}
	
	public Logger getLogger(String name) {
		if (log.equals(Logger.getLogger("TitanLog"))) { log = Logger.getLogger(name); }
		return log;
	}
	
	public String getName() {
		return name;
	}
	
	public static InputStream getResource(String fileName) {
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
	
	public boolean onCommand(Player player, String cmd, String[] args) { return false; }
	
	public static void register(Command command) {
		plugin.getCommandManager().register(command);
	}
	
	public void reloadConfig() {
		if (configFile == null) { configFile = new File(new File(plugin.getAddonDir(), name), "config.yml"); }
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = getResource("config.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	public void saveConfig() {
		if (config == null || configFile == null) { return; }
		try { config.save(configFile); } catch (IOException e) {}
	}
}