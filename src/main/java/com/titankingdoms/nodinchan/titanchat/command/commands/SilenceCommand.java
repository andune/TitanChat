package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class SilenceCommand extends Command {
	
	public SilenceCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.SILENCE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (plugin.has(player, "TitanChat.silence")) {
			try {
				if (cm.exists(args[0])) {
					Channel channel = cm.getChannel(args[0]);
					channel.setSilenced((channel.isSilenced()) ? false : true);
					
					for (String participant : cm.getChannel(args[0]).getParticipants()) {
						if (plugin.getPlayer(participant) != null) {
							if (channel.isSilenced())
								plugin.sendWarning(plugin.getPlayer(participant), "The channel has been silenced");
							else
								plugin.sendInfo(player, "The channel is no longer silenced");
						}
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} catch (IndexOutOfBoundsException e) {
				plugin.setSilenced((plugin.isSilenced()) ? false : true);
				
				for (Player receiver : plugin.getServer().getOnlinePlayers()) {
					if (plugin.isSilenced())
						plugin.sendWarning(receiver, "All channels have been silenced");
					else
						plugin.sendInfo(receiver, "Channels are no longer silenced");
				}
			}
		}
	}
}