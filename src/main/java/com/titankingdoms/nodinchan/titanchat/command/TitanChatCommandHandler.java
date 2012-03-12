package com.titankingdoms.nodinchan.titanchat.command;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;
import com.titankingdoms.nodinchan.titanchat.support.Addon;

public class TitanChatCommandHandler implements CommandExecutor {
	
	private TitanChat plugin;
	
	private ChannelManager cm;
	
	public TitanChatCommandHandler(TitanChat plugin) {
		this.plugin = plugin;
		this.cm = plugin.getChannelManager();
	}
	
	public void onCommand(Player player, String cmd, String[] args) {
		if (Commands.fromName(cmd) != null) {
			try { new com.titankingdoms.nodinchan.titanchat.command.commands.Command(Commands.fromName(cmd), plugin, cm).execute(player, args); } catch (Exception e) {}
			return;
			
		} else {
			if (runCommands(player, cmd, args))
				return;
		}
		
		plugin.sendWarning(player, "Invalid Command");
		plugin.sendInfo(player, "'/titanchat commands [page]' for command list");
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (cmd.getName().equals("titanchat")) {
			if (!(sender instanceof Player)) {
				if (args[0].equalsIgnoreCase("reload")) {
					plugin.log(Level.INFO, "Reloading configs...");
					
					plugin.reloadConfig();
					
					for (Channel channel : cm.getChannels()) {
						channel.reloadConfig();
					}
					
					cm.getChannels().clear();
					
					try { cm.loadChannels(); } catch (Exception e) {}
					
					plugin.log(Level.INFO, "Configs reloaded");
					return true;
				}
				
				plugin.log(Level.INFO, "Please use commands in-game");
				return true;
			}
			
			if (args.length < 1) {
				sender.sendMessage(ChatColor.AQUA + "TitanChat Commands");
				sender.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
				sender.sendMessage(ChatColor.AQUA + "Alias: /tc [command] [arguments]");
				plugin.sendInfo((Player) sender, "'/titanchat commands [page]' for command list");
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("titanchat")) {
				onCommand((Player) sender, args[0], parseCommand(args));
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("broadcast")) {
			if (!(sender instanceof Player)) {
				String message = plugin.getConfig().getString("broadcast.server");
				
				StringBuilder str = new StringBuilder();
				
				for (String word : args) {
					if (str.length() > 0)
						str.append(" ");
					
					str.append(word);
				}
				
				message = message.replace("%message", str.toString());
				
				plugin.getServer().broadcastMessage(plugin.getFormat().colourise(message));
				plugin.getLogger().info("<Server> " + plugin.getFormat().decolourise(str.toString()));
				return true;
			}
			
			if (plugin.has((Player) sender, "TitanChat.broadcast"))
				try { new com.titankingdoms.nodinchan.titanchat.command.commands.Command(Commands.BROADCAST, plugin, cm).execute((Player) sender, args); } catch (Exception e) {}
			else
				plugin.sendWarning((Player) sender, "You do not have permission");
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("me")) {
			if (!(sender instanceof Player)) {
				String message = plugin.getConfig().getString("emote.server");
				
				StringBuilder str = new StringBuilder();
				
				for (String word : args) {
					if (str.length() > 0)
						str.append(" ");
					
					str.append(word);
				}
				
				message = message.replace("%action", str.toString());
				plugin.getServer().broadcastMessage(plugin.getFormat().colourise(message));
				plugin.getLogger().info("* Server " + plugin.getFormat().decolourise(str.toString()));
				return true;
			}
			
			if (plugin.has((Player) sender, "TitanChat.emote.server"))
				try { new com.titankingdoms.nodinchan.titanchat.command.commands.Command(Commands.EMOTE, plugin, cm).execute((Player) sender, args); } catch (Exception e) {}
			else
				plugin.sendWarning((Player) sender, "You do not have permission");
			
			return true;
		}
		
		return false;
	}
	
	public String[] parseCommand(String[] args) {
		StringBuilder str = new StringBuilder();
		
		for (String arg : args) {
			if (str.length() > 0)
				str.append(" ");
			
			if (arg.equals(args[0]))
				continue;
			
			str.append(arg);
		}
		
		return (str.toString().equals("")) ? new String[] {} : str.toString().split(" ");
	}
	
	public boolean runCommands(Player player, String cmd, String[] args) {
		for (Addon addon : plugin.getAddons()) {
			if (addon.onCommand(player, cmd, args))
				return true;
		}
		
		for (Channel channel : plugin.getChannelManager().getChannels()) {
			if (channel instanceof CustomChannel) {
				if (((CustomChannel) channel).onCommand(player, cmd, args))
					return true;
			}
		}
		
		return false;
	}
}