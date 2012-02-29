package com.titankingdoms.nodinchan.titanchat.support;

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
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

public abstract class CustomChannel extends Channel {

	protected TitanChat plugin;
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	public CustomChannel(TitanChat plugin, String name) {
		super(plugin, name);
		this.plugin = plugin;
		setType("custom");
	}
	
	public String colourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	public String decolourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "");
	}
	
	public abstract String format(Player player, String message);
	
	public FileConfiguration getConfig() {
		if (config == null)
			reloadConfig();
		
		return config;
	}
	
	public File getDataFolder() {
		File dir = new File(plugin.getChannelsFolder(), getName());
		dir.mkdir();
		return dir;
	}
	
	public Logger getLogger(String name) {
		return Logger.getLogger(name);
	}
	
	public InputStream getResource(String filename) {
		File file = new File(plugin.getChannelsFolder(), plugin.getSupportLoader().getCustomChannelJar(getName()));
		
		try {
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();
			
			while (entries.hasMoreElements()) {
				JarEntry element = entries.nextElement();
				
				if (element.getName().equalsIgnoreCase(filename))
					return jarFile.getInputStream(element);
			}
			
		} catch (IOException e) {}
		
		return null;
	}
	
	public abstract void init();
	
	public abstract Channel load(Channel channel);
	
	public abstract boolean onJoin(Player player);
	
	public abstract void onLeave(Player player);
	
	public void reloadConfig() {
		if (configFile == null)
			configFile = new File(getDataFolder(), "config.yml");
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = getResource("config.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	public void saveConfig() {
		if (config == null || configFile == null)
			return;
		
		try { config.save(configFile); } catch (IOException e) {}
	}
	
	public abstract void sendMessage(Player player, String message);
}