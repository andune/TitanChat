package com.titankingdoms.nodinchan.titanchat.channel;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract interface CustomChannelInterface extends ChannelInterface {
	
	public abstract String colourise(String text);
	
	public abstract String decolourise(String text);
	
	public abstract String format(Player player, String message);
	
	public abstract FileConfiguration getConfig();
	
	public abstract File getDataFolder();
	
	public abstract Logger getLogger(String name);
	
	public abstract InputStream getResource(String fileName);
	
	public abstract void init();
	
	public boolean onCommand(Player player, String cmd, String[] args);
	
	public abstract void reloadConfig();
	
	public abstract void saveConfig();
}