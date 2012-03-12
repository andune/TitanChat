package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class EmoteCommand extends Command {

	public EmoteCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.EMOTE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1 || !plugin.has(player, "TitanChat.me")) { return; }
		
		StringBuilder meStr = new StringBuilder();
		
		for (String word : args) {
			if (meStr.length() > 1)
				meStr.append(" ");
			
			meStr.append(word);
		}
		
		cm.getChannel(player).sendMessage(plugin.getFormat().emoteFormat(player, meStr.toString()));
		plugin.getLogger().info("* " + player.getName() + " " + meStr.toString());
	}
}