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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Type;
import com.titankingdoms.nodinchan.titanchat.commands.TitanChatCommandHandler;
import com.titankingdoms.nodinchan.titanchat.support.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.support.Support;
import com.titankingdoms.nodinchan.titanchat.support.SupportLoader;
import com.titankingdoms.nodinchan.titanchat.util.ConfigManager;
import com.titankingdoms.nodinchan.titanchat.util.Format;

/*
 *     TitanChat 2.1.3
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
	private SupportLoader loader;
	
	private File channelConfigFile = null;
	private FileConfiguration channelConfig = null;
	
	private Channel defaultChannel = null;
	private Channel staffChannel = null;
	
	private boolean silence = false;
	
	private List<Channel> channels;
	private List<com.titankingdoms.nodinchan.titanchat.support.Command> cmds;
	private List<CustomChannel> customChannels;
	private List<Support> supports;
	
	private Permission perm;
	private Chat chat;
	
	public void assignAdmin(Player player, Channel channel) {
		channel.getAdminList().add(player.getName());
		sendInfo(player, "You are now an Admin of " + channel.getName());
	}
	
	public boolean channelExist(String channelName) {
		for (Channel channel : channels) {
			if (channel.getName().equalsIgnoreCase(channelName))
				return true;
		}
		
		return false;
	}
	
	public void channelSwitch(Player player, Channel oldCh, Channel newCh) {
		oldCh.leave(player);
		newCh.join(player);
	}
	
	public boolean correctPass(Channel channel, String password) {
		return channel.getPassword().equals(password);
	}
	
	public void createChannel(Player player, String channelName) {
		Channel channel = new Channel(this, channelName);
		channel.setType("public");
		channels.add(channel);
		
		assignAdmin(player, channel);
		channelSwitch(player, getChannel(player), channel);
		sendInfo(player, "You have created " + channel.getName() + " channel");
	}
	
	public String createList(List<String> list) {
		StringBuilder str = new StringBuilder();
		
		for (String item : list) {
			if (str.length() > 0)
				str.append(", ");
			
			str.append(item);
		}
		
		return str.toString();
	}
	
	public void deleteChannel(Player player, Channel channel) {
		for (String participant : channel.getParticipants()) {
			if (getPlayer(participant) != null) {
				channelSwitch(player, channel, getSpawnChannel(player));
				sendWarning(getPlayer(participant), channel.getName() + " has been deleted");
			}
		}
		
		channels.remove(channel);
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
		return channels.size() - customChannels.size();
	}
	
	public FileConfiguration getChannelConfig() {
		if (channelConfig == null) {
			reloadChannelConfig();
		}
		
		return channelConfig;
	}
	
	public File getChannelsFolder() {
		return new File(getDataFolder(), "channels");
	}
	
	public List<com.titankingdoms.nodinchan.titanchat.support.Command> getCommands() {
		return cmds;
	}
	
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	public List<Channel> getChannels() {
		return channels;
	}
	
	public CustomChannel getCustomChannel(Channel channel) {
		for (CustomChannel customChannel : customChannels) {
			if (customChannel.getName().equals(channel.getName()))
				return customChannel;
		}
		
		return null;
	}
	
	public Channel getDefaultChannel() {
		if (defaultChannel == null) {
			for (Channel channel : channels) {
				if (channel.getType().equals(Type.DEFAULT)) {
					defaultChannel = channel;
					break;
				}
			}
		}
		
		return defaultChannel;
	}
	
	public String getExactName(String channelName) {
		return getChannel(channelName).getName();
	}
	
	public Format getFormat() {
		return format;
	}
	
	public String getFormat(String channelName) {
		if (getConfig().get("channels." + getExactName(channelName) + ".format") != null && !getConfig().getString("channels." + getExactName(channelName) + ".format").equalsIgnoreCase(""))
			return getConfig().getString("channels." + getExactName(channelName) + ".format");
		
		return getConfig().getString("formatting.format");
	}
	
	public String getGroupPrefix(Player player) {
		String prefix = chat.getGroupPrefix(player.getWorld(), perm.getPrimaryGroup(player));
		
		if (prefix == null)
			return "";
		
		return prefix;
	}
	
	public String getGroupSuffix(Player player) {
		String suffix = chat.getGroupSuffix(player.getWorld(), perm.getPrimaryGroup(player));
		
		if (suffix == null)
			return "";
		
		return suffix;
	}
	
	public Player getPlayer(String name) {
		return getServer().getPlayer(name);
	}
	
	public String getPlayerPrefix(Player player) {
		return (chat.getPlayerPrefix(player) == null) ? "" : chat.getPlayerPrefix(player);
	}
	
	public String getPlayerSuffix(Player player) {
		return (chat.getPlayerSuffix(player) == null) ? "" : chat.getPlayerSuffix(player);
	}
	
	public Channel getSpawnChannel(Player player) {
		if (has(player, "TitanChat.admin") && has(player, "TitanChat.adminspawn")) {
			if (staffChannel != null)
				return staffChannel;
		}
		
		for (Channel channel : channels) {
			if (has(player, "TitanChat.spawn." + channel.getName()) && !has(player, "TitanChat.adminspawn." + channel.getName()))
				return channel;
		}
		
		return defaultChannel;
	}
	
	public Channel getStaffChannel() {
		if (staffChannel == null) {
			for (Channel channel : channels) {
				if (channel.getType().equals(Type.STAFF)) {
					staffChannel = channel;
					break;
				}
			}
		}
		
		return staffChannel;
	}
	
	public SupportLoader getSupportLoader() {
		return loader;
	}
	
	public List<Support> getSupports() {
		return supports;
	}
	
	public File getSupportsFolder() {
		return new File(getDataFolder(), "supports");
	}
	
	public boolean has(Player player, String permission) {
		if (perm != null)
			return perm.has(player, permission);
		
		return player.hasPermission(permission);
	}
	
	public boolean hasVoice(Player player) {
		return has(player, "TitanChat.voice");
	}
	
	public boolean isSilenced() {
		return silence;
	}
	
	public boolean isStaff(Player player) {
		if (has(player, "TitanChat.admin"))
			return true;
		
		return false;
	}
	
	public void log(Level level, String msg) {
		log.log(level, "[" + this + "] " + msg);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args[0].equalsIgnoreCase("reload")) {
				log(Level.INFO, "Reloading configs...");
				
				saveConfig();
				saveChannelConfig();
				
				reloadConfig();
				reloadChannelConfig();
				
				try { prepareChannels(); } catch (Exception e) {}
				
				log(Level.INFO, "Configs reloaded");
				return true;
			}
			
			log(Level.INFO, "Please use commands in-game");
			return true;
		}
		
		String[] arguments = parseCommand(args);
		
		Player player = (Player) sender;
		
		if (args.length < 1) {
			player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
			player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
			player.sendMessage(ChatColor.AQUA + "Alias: /tc [command] [arguments]");
			player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("titanchat")) {
			cmdHandler.onCommand(player, args[0], arguments);
			return true;
		}
		
		sendWarning(player, "Invalid Command");
		return false;
	}
	
	@Override
	public void onDisable() {
		log(Level.INFO, "Saving channel information...");
		
		for (Channel channel : channels) {
			if (channel.getType().equals(Type.CUSTOM))
				getCustomChannel(channel).unload();
			else
				channel.unload();
		}
		
		log(Level.INFO, "Clearing useless data...");
		
		channels.clear();
		cmds.clear();
		customChannels.clear();
		supports.clear();
		
		log(Level.INFO, "is now disabled");
	}
	
	@Override
	public void onEnable() {
		log(Level.INFO, "is now enabling...");
		
		log(Level.INFO, "Checking for Vault...");
		
		PluginManager pm = getServer().getPluginManager();
		
		if (pm.getPlugin("Vault") == null) {
			log(Level.WARNING, "Vault not found!");
			pm.disablePlugin(this);
			return;
		}
		
		if (setupPermission()) {
			log(Level.INFO, perm.getName() + " detected");
			log(Level.INFO, "Using " + perm.getName() + " for permissions");
		}
		
		if (setupChat()) {
			log(Level.INFO, "Prefixes and suffixes supported");
		}
		
		log(Level.INFO, "Vault hooked");
		
		cmdHandler = new TitanChatCommandHandler(this);
		configManager = new ConfigManager(this);
		format = new Format(this);
		
		channels = new ArrayList<Channel>();
		cmds = new ArrayList<com.titankingdoms.nodinchan.titanchat.support.Command>();
		customChannels = new ArrayList<CustomChannel>();
		supports = new ArrayList<Support>();
		
		File config = new File(getDataFolder(), "config.yml");
		File channelConfig = new File(getDataFolder(), "channel.yml");
		
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
		
		if (getChannelsFolder().mkdir())
			log(Level.INFO, "Loading channels folder...");
		
		if (getSupportsFolder().mkdir())
			log(Level.INFO, "Loading supports folder...");
		
		loader = new SupportLoader(this);
		
		try { supports.addAll(loader.loadSupports()); } catch (Exception e) {}
		
		try { prepareChannels(); } catch (Exception e) {}
		
		if (getDefaultChannel() == null) {
			log(Level.WARNING, "Default channel not defined");
			pm.disablePlugin(this);
			return;
		}
		
		pm.registerEvents(new TitanChatListener(this), this);
		
		log(Level.INFO, "is now enabled");
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
		
		return str.toString().split(" ");
	}
	
	public void prepareChannels() throws Exception {
		List<String> globalChannels = new ArrayList<String>();
		customChannels = loader.loadChannels();
		
		for (CustomChannel customChannel : customChannels) {
			Channel channel = new Channel(this, customChannel.getName());
			channel = customChannel.load(channel);
			channel.setType("custom");
			channels.add(channel);
		}
		
		for (String channelName : getConfig().getConfigurationSection("channels").getKeys(false)) {
			if (getChannel(channelName) != null)
				continue;
			
			Channel channel = new Channel(this, channelName);
			
			if (getChannelConfig().getStringList("channels." + channel.getName() + ".admins") != null)
				channel.getAdminList().addAll(getChannelConfig().getStringList("channels." + channel.getName() + ".admins"));
			
			if (getChannelConfig().getStringList("channels." + channel.getName() + ".whitelist") != null)
				channel.getWhiteList().addAll(getChannelConfig().getStringList("channels." + channel.getName() + ".whitelist"));
			
			if (getChannelConfig().getStringList("channels." + channel.getName() + ".blacklist") != null)
				channel.getBlackList().addAll(getChannelConfig().getStringList("channels." + channel.getName() + ".blacklist"));
			
			if (getChannelConfig().getStringList("channels." + channel.getName() + ".followers") != null)
				channel.getFollowers().addAll(getChannelConfig().getStringList("channels." + channel.getName() + ".followers"));
			
			channel.setType(getConfig().getString("channels." + channel.getName() + ".type"));
			
			if (channel.getType().equals(Type.PASSWORD))
				channel.setPassword(getConfig().getString("channels." + channel.getName() + ".password"));
			
			if (getConfig().get("channels." + channel.getName() + ".global") != null) {
				if (getConfig().getBoolean("channels." + channelName + ".global")) {
					channel.setGlobal(true);
					globalChannels.add(channelName);
				}
			}
			
			channels.add(channel);
		}
		
		log(Level.INFO, "No. of channels: " + getChannelAmount());
		log(Level.INFO, "No. of global broadcasting channels: " + globalChannels.size());
		log(Level.INFO, "TitanChat Channels Loaded");
	}
	
	public void registerCommand(com.titankingdoms.nodinchan.titanchat.support.Command cmd) {
		cmds.add(cmd);
	}
	
	public void reloadChannelConfig() {
		if (channelConfigFile == null) {
			channelConfigFile = new File(getDataFolder(), "channel.yml");
		}
		
		channelConfig = YamlConfiguration.loadConfiguration(channelConfigFile);
		
		InputStream defConfigStream = getResource("channel.yml");
		
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
			perm = permissionProvider.getProvider();
		}
		
		return (perm != null);
	}
	
	public boolean useDefaultFormat() {
		return getConfig().getBoolean("formatting.use-built-in");
	}
	
	public void whitelistMember(Player player, String channelName) {
		Channel channel = getChannel(channelName);
		channel.getWhiteList().add(player.getName());
		sendInfo(player, "You are now a Member of " + channel.getName());
	}
}