package com.titankingdoms.nodinchan.titanchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Status;
import com.titankingdoms.nodinchan.titanchat.commands.TitanChatCommandHandler;
import com.titankingdoms.nodinchan.titanchat.commands.TitanChatCommandHandler.Commands;
import com.titankingdoms.nodinchan.titanchat.support.SupportLoader;
import com.titankingdoms.nodinchan.titanchat.support.TCSupport;
import com.titankingdoms.nodinchan.titanchat.util.ConfigManager;
import com.titankingdoms.nodinchan.titanchat.util.Format;
import com.titankingdoms.nodinchan.titanchat.util.Settings;

/*
 *     TitanChat 2.0
 *     Copyright (C) 2012  Nodin Chan <nodinchan@nodinchan.net>
 *     
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class TitanChat extends JavaPlugin {
	
	protected static Logger log = Logger.getLogger("TitanLog");
	
	private TitanChatCommandHandler cmdHandler;
	private ConfigManager configManager;
	private Format format;
	private Settings settings;
	
	private File channelConfigFile = null;
	private FileConfiguration channelConfig = null;
	
	private boolean silence = false;
	
	private List<Channel> channels;
	private List<TCSupport> supports;
	
	private Permission permission;
	private Chat chat;
	
	public void assignAdmin(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getAdminList().add(player.getName());
		
		sendInfo(player, "You are now an Admin of " + channelName);
	}
	
	public void ban(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		
		channel.getAdminList().remove(player.getName());
		channel.getWhiteList().remove(player.getName());
		channel.getBlackList().add(player.getName());
		
		leaveChannel(player, channelName);
		sendWarning(player, "You have been banned from " + channelName);
	}
	
	public boolean channelExist(String channelName) {
		for (Channel channel : channels) {
			if (channel.getName().equalsIgnoreCase(channelName))
				return true;
		}
		
		return false;
	}
	
	public void channelSwitch(Player player, String oldCh, String newCh) {
		leaveChannel(player, oldCh);
		enterChannel(player, newCh);
	}
	
	public boolean correctPass(String channelName, String password) {
		return getChannel(channelName).getPassword().equals(password);
	}
	
	public void createChannel(Player player, String channelName) {
		Channel channel = new Channel(this, channelName);
		channel.setStatus("public");
		
		assignAdmin(player, channelName);
		enterChannel(player, channelName);
		sendInfo(player, "You have created " + channelName + " channel");
	}
	
	public String createList(List<String> channels) {
		StringBuilder str = new StringBuilder();
		
		for (String item : channels) {
			if (str.length() > 0) {
				str.append(", ");
			}
			
			str.append(item);
		}
		
		return str.toString();
	}
	
	public void deleteChannel(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		
		for (String participant : channel.getParticipants()) {
			if (getPlayer(participant) != null) {
				leaveChannel(getPlayer(participant), channelName);
				sendWarning(getPlayer(participant), channel.getName() + " has been deleted");
			}
		}
		
		channels.remove(channel);
	}
	
	public void demote(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		
		channel.getAdminList().remove(player.getName());
		channel.getWhiteList().add(player.getName());
		
		sendInfo(player, "You have been demoted in " + channelName);
	}
	
	public void enterChannel(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getParticipants().add(player.getName());
		
		if (settings.enableJoinMessages()) {
			for (String participant : channel.getParticipants()) {
				if (getPlayer(participant) != null && !getPlayer(participant).equals(player))
					sendInfo(getPlayer(participant), player.getDisplayName() + " has joined the channel");
			}
		}
	}
	
	public void follow(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getFollowers().add(player.getName());
		
		sendInfo(player, "You have unfollowed " + channel.getName());
	}
	
	public Channel getChannel(String channelName) {
		for (Channel channel : channels) {
			if (channel.getName().equalsIgnoreCase(channelName))
				return channel;
		}
		
		return null;
	}
	
	public Channel getChannel(Player player) {
		for (Channel channel : channels) {
			if (channel.getParticipants().contains(player.getName()))
				return channel;
		}
		
		return null;
	}
	
	public int getChannelAmount() {
		return channels.size() - 1;
	}
	
	public FileConfiguration getChannelConfig() {
		if (channelConfig == null) {
			reloadChannelConfig();
		}
		
		return channelConfig;
	}
	
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	protected List<Channel> getChannels() {
		return channels;
	}
	
	public Channel getDefaultChannel() {
		for (Channel channel : channels) {
			if (channel.getStatus().equals(Status.DEFAULT))
				return channel;
		}
		
		return null;
	}
	
	public Format getFormat() {
		return format;
	}
	
	public String getFormat(String channelName) {
		if (getConfig().get("channels." + channelName + ".format") != null && !getConfig().getString("channels." + channelName + ".format").equalsIgnoreCase(""))
			
			return getConfig().getString("channels." + channelName + ".format");
		
		return getConfig().getString("formatting.format");
	}
	
	public String getGroupPrefix(Player player) {
		return chat.getGroupPrefix(player.getWorld(), permission.getPrimaryGroup(player));
	}
	
	public String getGroupSuffix(Player player) {
		return chat.getGroupSuffix(player.getWorld(), permission.getPrimaryGroup(player));
	}
	
	public Player getPlayer(String name) {
		return getServer().getPlayer(name);
	}
	
	public String getPlayerPrefix(Player player) {
		return chat.getPlayerPrefix(player);
	}
	
	public String getPlayerSuffix(Player player) {
		return chat.getPlayerSuffix(player);
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public Channel getStaffChannel() {
		for (Channel channel : channels) {
			if (channel.getStatus().equals(Status.STAFF))
				return channel;
		}
		
		return null;
	}
	
	public List<TCSupport> getSupports() {
		return supports;
	}
	
	public boolean has(Player player, String permissionNode) {
		if (permission != null) {
			return permission.has(player, permissionNode);
		}
		
		return player.hasPermission(permissionNode);
	}
	
	public boolean hasVoice(Player player) {
		return has(player, "TitanChat.voice");
	}
	
	public void invite(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getInviteList().add(player.getName());
		
		sendInfo(player, "You have been invited to chat on " + channel.getName());
	}
	
	public void inviteResponse(Player player, String channelName, boolean accept) {
		Channel channel = getChannel(channelName);
		
		channel.getInviteList().remove(player.getName());
		
		if (accept) {
			enterChannel(player, channelName);
		}
	}
	
	public boolean isSilenced() {
		return silence;
	}
	
	public boolean isStaff(Player player) {
		if (has(player, "TitanChat.admin"))
			return true;
		
		return false;
	}
	
	public void leaveChannel(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getParticipants().remove(player.getName());
		
		if (settings.enableLeaveMessages()) {
			for (String participant : channel.getParticipants()) {
				if (getPlayer(participant) != null)
					sendInfo(getPlayer(participant), player.getDisplayName() + " has left the channel");
			}
		}
	}
	
	public void log(Level level, String msg) {
		log.log(level, "[" + this + "] " + msg);
	}
	
	public void mute(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getMuteList().add(player.getName());
		
		sendWarning(player, "You have been muted on " + channelName);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args[0].equalsIgnoreCase("reload")) {
				log(Level.INFO, "Reloading configs...");
				saveConfig();
				saveChannelConfig();
				reloadConfig();
				reloadChannelConfig();
				prepareChannelCommunities();
				log(Level.INFO, "Configs reloaded");
				return true;
			}
			log(Level.INFO, "Please use commands in-game");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("titanchat")) {
			if (Commands.fromName(args[0]) == null) {
				sendWarning(player, "Invalid Command");
				return false;
			}
			
			if (args.length < 2) {
				String channelName = getChannel(player).getName();
				
				switch (Commands.fromName(args[0])) {
				
				case CONVERTCOLOR:
				case CONVERTCOLOUR:
					if (has(player, "TitanChat.admin")) {
						configManager.setConvertColours(channelName, (format.colours(channelName)) ? false : true);
						sendInfo(player, "The channel now " + ((format.colours(channelName)) ? "converts" : "ignores") + " colour codes");
						
					} else {
						sendWarning(player, "You do not have permission to change this setting");
					}
					break;
					
				case COMMANDS:
					player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
					player.sendMessage(ChatColor.AQUA + "Command: /titanchat [action] [argument]");
					player.sendMessage(ChatColor.AQUA + "Alias: /tc action [argument]");
					player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					break;
					
				case INFO:
					String participantList = "";
					String followerList = "";
					
					if (getChannel(channelName).getParticipants().isEmpty())
						participantList = "None";
					else
						participantList = createList(getChannel(channelName).getParticipants());
					
					if (getChannel(channelName).getFollowers().isEmpty())
						followerList = "None";
					else
						followerList = createList(getChannel(channelName).getFollowers());
					
					player.sendMessage(ChatColor.AQUA + "Participants: " + participantList);
					player.sendMessage(ChatColor.AQUA + "Followers: " + followerList);
					break;
					
				case LIST:
					List<String> channelList = new ArrayList<String>();
					
					for (Channel channel : channels) {
						if (channel.canAccess(player)) {
							channelList.add(channel.getName());
						}
					}
					
					if (channelList.isEmpty())
						sendInfo(player, "Channel list: None");
					else
						sendInfo(player, "Channel list: " + createList(channelList));
					break;
					
				case RELOAD:
					if (isStaff(player)) {
						log(Level.INFO, "Reloading configs...");
						sendInfo(player, "Reloading configs...");
						saveConfig();
						saveChannelConfig();
						reloadConfig();
						reloadChannelConfig();
						prepareChannelCommunities();
						log(Level.INFO, "Configs reloaded");
						sendInfo(player, "Configs reloaded");
					}
					break;
					
				case SILENCE:
					silence = (silence) ? false : true;
					
					for (Player receiver : getServer().getOnlinePlayers()) {
						if (silence) {
							sendWarning(receiver, "All channels have been silenced");
							
						} else {
							sendInfo(receiver, "Channels are no longer silenced");
						}
					}
					break;
				}
				
				return true;
			}
			
			if (args.length < 3) {
				cmdHandler.onCommand(player, args[0], args[1]);
				return true;
			}
			
			if (args.length < 4) {
				cmdHandler.onCommand(player, args[0], args[1], args[2]);
				return true;
			}
			
			if (args.length > 3) {
				switch (Commands.fromName(args[0])) {
				
				case BROADCAST:
					if (player.hasPermission("TitanChat.broadcast")) {
						StringBuilder str = new StringBuilder();
						
						for (int word = 1; word < args.length; word++) {
							if (str.length() > 0) {
								str.append(" ");
							}
							
							str.append(args[word]);
						}
						
						String msg = format.broadcast(player, format.filter(str.toString()));
						
						getServer().broadcastMessage(msg);
						
						log.info("<" + player.getName() + "> " + str.toString());
						
					} else {
						sendWarning(player, "You do not have permission to broadcast");
					}
					break;
					
				case COMMANDS:
					if (args[1].equalsIgnoreCase("1")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (1/6) ==");
						player.sendMessage(ChatColor.AQUA + "accept [channel] - Accepts the channel join invitation and joins the channel");
						player.sendMessage(ChatColor.AQUA + "add [player] - Adds the player to the whitelist");
						player.sendMessage(ChatColor.AQUA + "ban [player] - Bans the player from the channel");
						player.sendMessage(ChatColor.AQUA + "broadcast [message] - Broadcasts the message globally");
						player.sendMessage(ChatColor.AQUA + "chcolour [colourcode] - Sets the display colour of the channel; Alias: chcolor");
						
					} else if (args[1].equalsIgnoreCase("2")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (2/6) ==");
						player.sendMessage(ChatColor.AQUA + "convertcolour [channel] - Sets whether colour codes will be converted on the channel; Alias: convertcolor");
						player.sendMessage(ChatColor.AQUA + "create [channel] - Creates a channel by that name");
						player.sendMessage(ChatColor.AQUA + "decline [channel] - Declines the channel join invitation");
						player.sendMessage(ChatColor.AQUA + "delete [channel] - Deletes the channel with that name");
						player.sendMessage(ChatColor.AQUA + "demote [player] - Demotes the player on the channel");
						
					} else if (args[1].equalsIgnoreCase("3")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (3/6) ==");
						player.sendMessage(ChatColor.AQUA + "filter [phrase] - Adds the phrase to the filter");
						player.sendMessage(ChatColor.AQUA + "follow [channel] - Follows the channel and receive chat");
						player.sendMessage(ChatColor.AQUA + "force [player] - Forces the player to join the channel");
						player.sendMessage(ChatColor.AQUA + "format [format] - Sets the format of the channel");
						player.sendMessage(ChatColor.AQUA + "info [channel] - Gives the participants and followers of the channel");
						
					} else if (args[1].equalsIgnoreCase("4")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (4/6) ==");
						player.sendMessage(ChatColor.AQUA + "invite [player] - Invites the player to join the channel");
						player.sendMessage(ChatColor.AQUA + "join [channel] - Joins the channel");
						player.sendMessage(ChatColor.AQUA + "kick [player] - Kicks the player from the channel");
						player.sendMessage(ChatColor.AQUA + "list - Lists all channels you have access to");
						player.sendMessage(ChatColor.AQUA + "mute [player] - Mutes the player on the channel");
						
					} else if (args[1].equalsIgnoreCase("5")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (5/6) ==");
						player.sendMessage(ChatColor.AQUA + "ncolour [colourcode] - Sets the display colour of the player name; Alias: ncolor");
						player.sendMessage(ChatColor.AQUA + "password [password] - Sets the password of the channel");
						player.sendMessage(ChatColor.AQUA + "promote [player] - Promotes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "reload - Reloads the configs");
						player.sendMessage(ChatColor.AQUA + "silence [channel] - Silences the channel; Leave out [channel] to silence all");
						
					} else if (args[1].equalsIgnoreCase("6")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (6/6) ==");
						player.sendMessage(ChatColor.AQUA + "status [password/private/public] - Sets the state of the channel");
						player.sendMessage(ChatColor.AQUA + "tag [tag] - Sets the channel tag");
						player.sendMessage(ChatColor.AQUA + "unban [player] - Unbans the player from the channel");
						player.sendMessage(ChatColor.AQUA + "unmute [player] - Unmutes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "unfollow [channel] - Unfollows the channel");
						
					} else {
						player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
						player.sendMessage(ChatColor.AQUA + "Command: /titanchat [action] [argument]");
						player.sendMessage(ChatColor.AQUA + "Alias: /tc action [argument]");
						player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					}
					break;
					
				case FILTER:
					if (isStaff(player)) {
						StringBuilder str = new StringBuilder();
						
						for (int word = 1; word < args.length; word++) {
							if (str.length() > 0) {
								str.append(" ");
							}
							
							str.append(args[word]);
						}
						
						configManager.filter(str.toString());
						sendInfo(player, "'" + str.toString() + "' has been filtered");
						
					} else {
						sendWarning(player, "You do not have permission to use this command");
					}
					break;
					
				case FORMAT:
					StringBuilder str = new StringBuilder();
					
					for (String word : args) {
						if (str.length() > 0) {
							str.append(" ");
						}
						
						str.append(word);
					}
					
					cmdHandler.getMCH().getChannelSettings().format(player, str.toString(), getChannel(player).getName());
					break;
				}
				
				return true;
			}
			
			sendWarning(player, "Invalid Command/Argument");
		}
		
		return false;
	}
	
	@Override
	public void onDisable() {
		log(Level.INFO, "Clearing useless data...");
		
		channels.clear();
		supports.clear();
		
		log(Level.INFO, "is now disabled");
	}
	
	@Override
	public void onEnable() {
		log(Level.INFO, "is now enabling...");
		
		log(Level.INFO, "Checking for Vault...");
		
		PluginManager pm = getServer().getPluginManager();
		
		if (!vault()) {
			log(Level.WARNING, "Vault not found!");
			pm.disablePlugin(this);
			return;
		}
		
		cmdHandler = new TitanChatCommandHandler(this);
		configManager = new ConfigManager(this);
		format = new Format(this);
		settings = new Settings(this);

		channels = new ArrayList<Channel>();
		supports = new ArrayList<TCSupport>();
		
		prepareChannelCommunities();
		
		if (getDefaultChannel() == null) {
			log(Level.WARNING, "Default channel not defined");
			pm.disablePlugin(this);
			return;
		}
		
		if (setupPermission()) {
			log(Level.INFO, permission.getName() + " detected");
			log(Level.INFO, "Using " + permission.getName() + " for permissions");
		}
		
		if (setupChat()) {
			log(Level.INFO, "Prefixes and suffixes supported");
		}
		
		log(Level.INFO, "Vault hooked");
		
		File config = new File(getDataFolder(), "config.yml");
		File channelConfig = new File(getDataFolder(), "channels.yml");
		File dir = new File(getDataFolder(), "supports");
		
		if (!config.exists()) {
			log(Level.INFO, "Loading default config");
			getConfig().options().copyDefaults(true);
			getChannelConfig().options().copyHeader(true);
			saveConfig();
		}
		
		if (!channelConfig.exists()) {
			log(Level.INFO, "Loading default channel players config");
			getChannelConfig().options().copyDefaults(true);
			getChannelConfig().options().copyHeader(true);
			saveChannelConfig();
		}
		
		if (!dir.exists()) {
			log(Level.INFO, "Loading support folder");
			dir.mkdir();
		}
		
		pm.registerEvents(new TitanChatPlayerListener(this), this);
		
		try { supports.addAll(new SupportLoader(this).load()); } catch (Exception e) {}
		
		log(Level.INFO, "is now enabled");
	}
	
	public void prepareChannelCommunities() {
		List<String> globalChannels = new ArrayList<String>();
		
		for (String channelName : getConfig().getConfigurationSection("channels").getKeys(false)) {
			if (channelName.equalsIgnoreCase("Local"))
				continue;
			
			Channel channel = new Channel(this, channelName);
			
			if (getChannelConfig().getStringList("channels." + channelName + ".admins") != null) {
				for (String name : getChannelConfig().getStringList("channels." + channelName + ".admins")) {
					channel.getAdminList().add(name);
				}
			}
			
			if (getChannelConfig().getStringList("channels." + channelName + ".whitelist") != null) {
				for (String name : getChannelConfig().getStringList("channels." + channelName + ".whitelist")) {
					channel.getWhiteList().add(name);
				}
			}
			
			if (getChannelConfig().getStringList("channels." + channelName + ".blacklist") != null) {
				for (String name : getChannelConfig().getStringList("channels." + channelName + ".blacklist")) {
					channel.getBlackList().add(name);
				}
			}
			
			if (getChannelConfig().getStringList("channels." + channelName + ".followers") != null) {
				for (String name : getChannelConfig().getStringList("channels." + channelName + ".followers")) {
					channel.getFollowers().add(name);
				}
			}
			
			channel.setStatus(getConfig().getString("channels." + channelName + ".status"));
			
			if (channel.getStatus().equals(Status.PASSWORD)) {
				String password = getConfig().getString("channels." + channelName + ".password");
				channel.setPassword(password);
			}
			
			if (getConfig().get("channels." + channelName + ".global") != null) {
				if (getConfig().getBoolean("channels." + channelName + ".global")) {
					channel.setGlobal(true);
					globalChannels.add(channelName);
				}
			}
			
			channels.add(channel);
		}
		
		Channel local = new Channel(this, "Local");
		local.setStatus("local");
		local.setRadius(getConfig().getInt("local.radius"));
		channels.add(local);
		
		log(Level.INFO, "No. of channels: " + (channels.size() - 1));
		log(Level.INFO, "No. of global broadcasting channels: " + globalChannels.size());
		log(Level.INFO, "TitanChat Communities Loaded");
	}
	
	public void promote(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		
		channel.getWhiteList().remove(player.getName());
		
		assignAdmin(player, channelName);
		sendInfo(player, "You have been promoted in " + channelName);
	}
	
	public void reloadChannelConfig() {
		if (channelConfigFile == null) {
			channelConfigFile = new File(getDataFolder(), "channels.yml");
		}
		
		channelConfig = YamlConfiguration.loadConfiguration(channelConfigFile);
		
		InputStream defConfigStream = getResource("channels.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			channelConfig.setDefaults(defConfig);
		}
	}
	
	public void saveChannelConfig() {
		if (channelConfig == null || channelConfigFile == null)
			return;
		
		try {
			channelConfig.save(channelConfigFile);
			
		} catch (IOException e) {
			log.severe("Could not save config to " + channelConfigFile);
		}
	}
	
	public void sendInfo(Player player, String info) {
		player.sendMessage("[TitanChat] " + ChatColor.GOLD + info);
	}
	
	public void sendWarning(Player player, String warning) {
		player.sendMessage("[TitanChat] " + ChatColor.RED + warning);
	}
	
	public void setSilence(boolean silence) {
		this.silence = silence;
	}
	
	public boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		
		return (chat != null);
	}
	
	public boolean setupPermission() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		
		return (permission != null);
	}
	
	public void unban(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		
		channel.getBlackList().remove(player.getName());
		
		whitelistMember(player, channelName);
		sendInfo(player, "You have been unbanned from " + channel.getName());
	}
	
	public void unfollow(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getFollowers().remove(player.getName());
		
		sendInfo(player, "You have unfollowed " + channel.getName());
	}
	
	public void unmute(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getMuteList().remove(player.getName());
		
		sendInfo(player, "You have been unmuted on " + channel.getName());
	}
	
	public boolean useDefaultFormat() {
		return getConfig().getBoolean("formatting.use-built-in");
	}
	
	public boolean vault() {
		return getServer().getPluginManager().getPlugin("Vault") != null;
	}
	
	public void whitelistMember(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getWhiteList().add(player.getName());
		
		sendInfo(player, "You are now a Member of " + channel.getName());
	}
}