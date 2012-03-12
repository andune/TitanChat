package com.titankingdoms.nodinchan.titanchat.support;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract interface AddonInterface {
	
	public void chatMade(String name, String message);
	
	public String chatMade(Player player, String message);
	
	public FileConfiguration getConfig();
	
	public File getDataFolder();
	
	public Logger getLogger(String name);
	
	public String getName();
	
	public InputStream getResource(String fileName);
	
	public void init();
	
	public boolean onCommand(Player player, String cmd, String[] args);
	
	public void reloadConfig();
	
	public void saveConfig();
}