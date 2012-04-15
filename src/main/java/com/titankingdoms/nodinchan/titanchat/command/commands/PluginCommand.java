package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.addon.AddonManager;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

/**
 * PluginCommand - Plugin maintenance commands
 * 
 * @author NodinChan
 *
 */
public class PluginCommand extends Command {
	
	private AddonManager am;
	private ChannelManager cm;
	
	public PluginCommand() {
		this.am = plugin.getAddonManager();
		this.cm = plugin.getChannelManager();
	}
	
	@CommandID(name = "Debug", triggers = "debug")
	@CommandInfo(description = "Toggles the debug", usage = "debug [type]")
	public void debug(Player player, String[] args) {
		try {
			if (plugin.isStaff(player)) {
				if (args[0].equalsIgnoreCase("none")) {
					Debugger.disable();
					
				} else if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("full")) {
					Debugger.enableAll();
					
				} else {
					for (String id : args[0].split(",")) {
						Debugger.enable(id);
					}
				}
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Debug"); }
	}
	
	@CommandID(name = "Reload", triggers = "reload")
	@CommandInfo(description = "Reloads the config", usage = "reload")
	public void reload(Player player, String[] args) {
		if (plugin.isStaff(player)) {
			plugin.log(Level.INFO, "Reloading configs...");
			plugin.sendInfo(player, "Reloading configs...");
			plugin.reloadConfig();
			am.reload();
			cm.reload();
			plugin.log(Level.INFO, "Configs reloaded");
			plugin.sendInfo(player, "Configs reloaded");
			
		} else { plugin.sendWarning(player, "You do not have permission"); }
	}
}