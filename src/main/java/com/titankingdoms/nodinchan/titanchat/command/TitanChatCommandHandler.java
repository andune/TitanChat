package com.titankingdoms.nodinchan.titanchat.command;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.support.Addon;

public class TitanChatCommandHandler implements CommandExecutor {
	
	private TitanChat plugin;
	
	private CommandManager cm;
	
	public TitanChatCommandHandler(TitanChat plugin) {
		this.plugin = plugin;
		this.cm = plugin.getCommandManager();
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (cmd.getName().equals("titanchat")) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.AQUA + "TitanChat Commands");
				sender.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
				sender.sendMessage(ChatColor.AQUA + "Alias: /tc [command] [arguments]");
				plugin.sendInfo((Player) sender, "'/titanchat commands [page]' for command list");
				return true;
			}
			
			if (!(sender instanceof Player)) {
				if (args[0].equalsIgnoreCase("reload")) {
					plugin.log(Level.INFO, "Reloading configs...");
					plugin.reloadConfig();
					for (Channel channel : plugin.getChannelManager().getChannels()) { channel.reloadConfig(); }
					plugin.getChannelManager().getChannels().clear();
					try { plugin.getChannelManager().loadChannels(); } catch (Exception e) {}
					plugin.log(Level.INFO, "Configs reloaded");
					return true;
				}
				
				plugin.log(Level.INFO, "Please use commands in-game"); return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("titanchat")) {
				cm.execute((Player) sender, args[0], parseCommand(args));
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
				try { cm.execute((Player) sender, "broadcast", args); } catch (Exception e) {}
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
				try { cm.execute((Player) sender, "me", args); } catch (Exception e) {}
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