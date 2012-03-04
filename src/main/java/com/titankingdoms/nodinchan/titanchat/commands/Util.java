package com.titankingdoms.nodinchan.titanchat.commands;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class Util {
	
	private TitanChat plugin;
	
	public Util(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public void reload(Player player) {
		if (plugin.isStaff(player)) {
			plugin.log(Level.INFO, "Reloading configs...");
			plugin.sendInfo(player, "Reloading configs...");
			
			plugin.reloadConfig();
			plugin.reloadChannelConfig();
			
			plugin.getChannels().clear();
			
			try { plugin.prepareChannels(); } catch (Exception e) {}
			
			plugin.log(Level.INFO, "Configs reloaded");
			plugin.sendInfo(player, "Configs reloaded");
			
		} else {
			plugin.sendWarning(player, "You do not have permission");
		}
	}
}