package com.titankingdoms.nodinchan.titanchat.channel;

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

public class CustomChannel extends Channel {

	protected TitanChat plugin;
	
	private Logger log = Logger.getLogger("TitanLog");
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	public CustomChannel(TitanChat plugin, String name) {
		super(plugin, name, Type.CUSTOM);
		this.plugin = plugin;
	}
	
	public String colourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	public String decolourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "");
	}
	
	public String format(Player player, String message) {
		return "<" + player.getDisplayName() + "> " + message;
	}
	
	@Override
	public FileConfiguration getConfig() {
		if (config == null)
			reloadConfig();
		
		return config;
	}
	
	public File getDataFolder() {
		File dir = new File(plugin.getChannelDir(), getName());
		dir.mkdir();
		return dir;
	}
	
	public Logger getLogger(String name) {
		if (log.equals(Logger.getLogger("TitanLog"))) { log = Logger.getLogger(name); }
		return log;
	}
	
	public InputStream getResource(String fileName) {
		try {
			JarFile jarFile = new JarFile(new File(plugin.getChannelDir(), plugin.getLoader().getCustomChannelJar(getName())));
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
	
	@Override
	public void reloadConfig() {
		if (configFile == null) { configFile = new File(getDataFolder(), "config.yml"); }
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = getResource("config.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	@Override
	public void saveConfig() {
		if (config == null || configFile == null)
			return;
		
		try { config.save(configFile); } catch (IOException e) {}
	}
}