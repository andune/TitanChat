package com.titankingdoms.nodinchan.titanchat;

import java.util.logging.Level;

import com.nodinchan.ncbukkit.NCBL;
import com.titankingdoms.nodinchan.titanchat.addon.AddonManager;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.CommandManager;

public class TitanChatManager {
	
	private final TitanChat plugin;
	
	private AddonManager addonManager;
	private ChannelManager chManager;
	private CommandManager cmdManager;
	
	public TitanChatManager() {
		this.plugin = TitanChat.getInstance();
		addonManager = new AddonManager();
		chManager = new ChannelManager();
		cmdManager = new CommandManager();
	}
	
	/**
	 * Gets the AddonManager
	 * 
	 * @return The AddonManager
	 */
	public AddonManager getAddonManager() {
		return addonManager;
	}
	
	/**
	 * Gets the ChannelManager
	 * 
	 * @return The ChannelManager
	 */
	public ChannelManager getChannelManager() {
		return chManager;
	}
	
	/**
	 * Gets the CommandManager
	 * 
	 * @return The CommandManager
	 */
	public CommandManager getCommandManager() {
		return cmdManager;
	}
	
	/**
	 * Loads the AddonManager, ChannelManager and CommandManager
	 */
	public void load() {
		NCBL ncbl = (NCBL) plugin.getServer().getPluginManager().getPlugin("NC-BukkitLib");
		
		if (ncbl != null) {
			addonManager.load();
			try { chManager.load(); } catch (Exception e) { e.printStackTrace(); plugin.log(Level.WARNING, "Channels failed to load"); }
			cmdManager.load();
		}
	}
	
	public void reload() {
		addonManager.preReload();
		chManager.preReload();
		cmdManager.preReload();
		addonManager.postReload();
		chManager.postReload();
		cmdManager.postReload();
	}
}