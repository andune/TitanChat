package com.titankingdoms.nodinchan.titanchat.channel.standard;

import org.bukkit.command.CommandSender;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.setting.Setting;

final class StandardSetting {
	
	public static void load(StandardChannel channel) {
		if (channel == null)
			return;
		
		channel.registerSettings(new Setting(channel, "Colour") {
			
			@Override
			public void set(CommandSender sender, String[] args) {
				if (args.length < 2) {
					plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
					plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat set <@><channel> colour [chat/name] [colour]");
					return;
				}
				
				if (args[0].equalsIgnoreCase("chat")) {
					channel.getInfo().setChatColour(args[1]);
					
				} else if (args[0].equalsIgnoreCase("name")) {
					channel.getInfo().setNameColour(args[1]);
					
				} else {
					plugin.send(MessageLevel.WARNING, sender, "Invalid Colour Setting");
					plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat set <@><channel> colour [chat/name] [colour]");
				}
			}
			
		}, new Setting(channel, "Colouring") {
			
			@Override
			public void set(CommandSender sender, String[] args) {
				
			}
			
		}, new Setting(channel, "") {
			
			@Override
			public void set(CommandSender sender, String[] args) {
				
			}
			
		}, new Setting(channel, "") {
			
			@Override
			public void set(CommandSender sender, String[] args) {
				
			}
			
		}, new Setting(channel, "") {
			
			@Override
			public void set(CommandSender sender, String[] args) {
				
			}
			
		}, new Setting(channel, "") {
			
			@Override
			public void set(CommandSender sender, String[] args) {
				
			}
			
		}, new Setting(channel, "") {
			
			@Override
			public void set(CommandSender sender, String[] args) {
				
			}
			
		});
	}
}