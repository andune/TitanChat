package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

public class PluginCommand extends Command {
	
	private ChannelManager cm;
	
	public PluginCommand(TitanChat plugin) {
		super(plugin);
		this.cm = plugin.getChannelManager();
	}
	
	@CommandID(name = "Debug", triggers = "debug")
	@CommandInfo(description = "Toggles the debug", usage = "debug [type]")
	public void debug(Player player, String[] args) {
		if (plugin.isStaff(player)) {
			if (args[0].equalsIgnoreCase("none")) {
				Debugger.disable();
				
			} else if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("full")) {
				Debugger.enableAll();
				
			} else {
				try {
					int debug = Integer.parseInt(args[0]);
					
					if (debug > 5 || debug < 1) {
						plugin.sendWarning(player, "Debug must be larger than 0 and smaller than 6");
						return;
					}
					
					Debugger.enable(debug);
					
				} catch (NumberFormatException e) { plugin.sendWarning(player, "Invalid debug argument"); }
			}
			
		} else { plugin.sendWarning(player, "You do not have permission"); }
	}
	
	@CommandID(name = "Reload", triggers = "reload")
	@CommandInfo(description = "Reloads the config", usage = "reload")
	public void reload(Player player, String[] args) {
		if (plugin.isStaff(player)) {
			plugin.log(Level.INFO, "Reloading configs...");
			plugin.sendInfo(player, "Reloading configs...");
			plugin.reloadConfig();
			cm.reload();
			plugin.log(Level.INFO, "Configs reloaded");
			plugin.sendInfo(player, "Configs reloaded");
			
		} else { plugin.sendWarning(player, "You do not have permission"); }
	}
}