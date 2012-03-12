package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class ReloadCommand extends Command {
	
	public ReloadCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.RELOAD, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (plugin.isStaff(player)) {
			plugin.log(Level.INFO, "Reloading configs...");
			plugin.sendInfo(player, "Reloading configs...");
			
			plugin.reloadConfig();
			
			ChannelManager chManager = plugin.getChannelManager();
			
			for (Channel channel : chManager.getChannels()) {
				channel.reloadConfig();
			}
			
			chManager.getChannels().clear();
			
			try { chManager.loadChannels(); } catch (Exception e) {}
			
			plugin.log(Level.INFO, "Configs reloaded");
			plugin.sendInfo(player, "Configs reloaded");
			
		} else {
			plugin.sendWarning(player, "You do not have permission");
		}
	}
}