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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.TitanChatCommandHandler;
import com.titankingdoms.nodinchan.titanchat.permissions.MiniPerms;
import com.titankingdoms.nodinchan.titanchat.permissions.hook.PermissionsHook;
import com.titankingdoms.nodinchan.titanchat.support.Addon;
import com.titankingdoms.nodinchan.titanchat.support.Loader;
import com.titankingdoms.nodinchan.titanchat.util.Format;

/*
 *     TitanChat 2.2
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
	private ChannelManager chManager;
	private Format format;
	private MiniPerms miniPerms;
	private PermissionsHook permHook;
	private Loader loader;
	
	private boolean silenced = false;
	
	private List<Addon> addons;
	
	private File permissionsFile = null;
	private FileConfiguration permissions = null;
	
	private Permission perm;
	private Chat chat;
	
	public void assignAdmin(Player player, Channel channel) {
		channel.getAdminList().add(player.getName());
		channel.save();
		sendInfo(player, "You are now an Admin of " + channel.getName());
	}
	
	public void channelSwitch(Player player, Channel oldCh, Channel newCh) {
		oldCh.leave(player);
		newCh.join(player);
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
	
	public boolean enableChannels() {
		return getConfig().getBoolean("chManager.getChannels().enable-channels");
	}
	
	public boolean enableJoinMessage() {
		return getConfig().getBoolean("chManager.getChannels().channel-messages.join");
	}
	
	public boolean enableLeaveMessage() {
		return getConfig().getBoolean("chManager.getChannels().channel-messages.leave");
	}
	
	public File getAddonDir() {
		return new File(getDataFolder(), "addons");
	}
	
	public List<Addon> getAddons() {
		return addons;
	}
	
	public File getChannelDir() {
		return new File(getDataFolder(), "channels");
	}
	
	public ChannelManager getChannelManager() {
		return chManager;
	}
	
	public File getCustomChannelDir() {
		return new File(getAddonDir(), "channels");
	}
	
	public Format getFormat() {
		return format;
	}
	
	public String getGroupPrefix(Player player) {
		if (chat != null) {
			String prefix = chat.getGroupPrefix(player.getWorld(), perm.getPrimaryGroup(player));
			return (prefix != null) ? prefix : "";
		}
		
		return permHook.getGroupPrefix(player);
	}
	
	public String getGroupSuffix(Player player) {
		if (chat != null) {
			String suffix = chat.getGroupSuffix(player.getWorld(), perm.getPrimaryGroup(player));
			return (suffix != null) ? suffix : "";
		}
		
		return permHook.getGroupSuffix(player);
	}
	
	public Loader getLoader() {
		return loader;
	}
	
	public MiniPerms getMiniPerms() {
		return miniPerms;
	}
	
	public FileConfiguration getPermissions() {
		if (permissions == null) { reloadPermissions(); }
		return permissions;
	}
	
	public Player getPlayer(String name) {
		return getServer().getPlayer(name);
	}
	
	public String getPlayerPrefix(Player player) {
		if (chat != null) {
			String prefix = chat.getPlayerPrefix(player.getWorld(), player.getName());
			return (prefix != null) ? prefix : "";
		}
		
		return permHook.getPlayerPrefix(player);
	}
	
	public String getPlayerSuffix(Player player) {
		if (chat != null) {
			String suffix = chat.getPlayerSuffix(player.getWorld(), player.getName());
			return (suffix != null) ? suffix : "";
		}
		
		return permHook.getPlayerSuffix(player);
	}
	
	public boolean has(Player player, String permission) {
		if (perm != null)
			return perm.has(player, permission);
		
		return permHook.has(player, permission);
	}
	
	public boolean hasVoice(Player player) {
		return has(player, "TitanChat.voice");
	}
	
	public boolean isSilenced() {
		return silenced;
	}
	
	public boolean isStaff(Player player) {
		return has(player, "TitanChat.admin");
	}
	
	public void log(Level level, String msg) {
		log.log(level, "[" + this + "] " + msg);
	}
	
	@Override
	public void onDisable() {
		log(Level.INFO, "is now disabling...");
		
		log(Level.INFO, "Saving channel information...");
		
		for (Channel channel : chManager.getChannels()) {
			channel.save();
		}
		
		log(Level.INFO, "Clearing ...");
		
		chManager.getChannels().clear();
		addons.clear();
		
		log(Level.INFO, "is now disabled");
	}
	
	@Override
	public void onEnable() {
		log(Level.INFO, "is now enabling...");
		
		cmdHandler = new TitanChatCommandHandler(this);
		chManager = new ChannelManager(this);
		format = new Format(this);
		miniPerms = new MiniPerms(this);
		permHook = new PermissionsHook(this);
		
		addons = new ArrayList<Addon>();
		
		File config = new File(getDataFolder(), "config.yml");
		File permissions = new File(getDataFolder(), "miniperms.yml");
		
		if (!config.exists()) {
			log(Level.INFO, "Loading default config");
			saveResource("config.yml", false);
		}
		
		if (!permissions.exists()) {
			log(Level.INFO, "Generating easy to use TitanChat MiniPerms...");
			saveResource("miniperms.yml", false);
		}
		
		if (getAddonDir().mkdir())
			log(Level.INFO, "Creating addon directory...");
		
		if (getCustomChannelDir().mkdir())
			log(Level.INFO, "Creating custom channel directory...");
		
		if (getChannelDir().mkdir()) {
			log(Level.INFO, "Creating channel directory...");
			saveResource("channels/Default.yml", false);
			saveResource("channels/Password.yml", false);
			saveResource("channels/Private.yml", false);
			saveResource("channels/Public.yml", false);
			saveResource("channels/README.yml", false);
			saveResource("channels/Staff.yml", false);
		}
		
		loader = new Loader(this);
		
		PluginManager pm = getServer().getPluginManager();
		
		if (pm.getPlugin("Vault") != null) {
			setupChatService();
			setupPermissionService();
		}
		
		miniPerms.load();
		
		pm.registerEvents(permHook, this);
		pm.registerEvents(new TitanChatListener(this), this);
		
		getCommand("titanchat").setExecutor(cmdHandler);
		getCommand("broadcast").setExecutor(cmdHandler);
		getCommand("me").setExecutor(cmdHandler);
		
		try { addons.addAll(loader.loadAddons()); } catch (Exception e) {}
		
		try { chManager.loadChannels(); } catch (Exception e) {}
		
		if (chManager.getDefaultChannel() == null) {
			log(Level.SEVERE, "A default channel not defined");
			pm.disablePlugin(this);
			return;
		}
		
		for (Player player : getServer().getOnlinePlayers()) {
			chManager.getSpawnChannel(player).join(player);
		}
		
		log(Level.INFO, "is now enabled");
	}
	
	public void reloadPermissions() {
		if (permissionsFile == null) { permissionsFile = new File(getDataFolder(), "miniperms.yml"); }
		
		permissions = YamlConfiguration.loadConfiguration(permissionsFile);
		
		InputStream defConfigStream = getResource("miniperms.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			permissions.setDefaults(defConfig);
		}
	}
	
	public void savePermissions() {
		if (permissionsFile == null || permissions == null) { return; }
		try { permissions.save(permissionsFile); } catch (IOException e) { log(Level.SEVERE, "Could not save to " + permissionsFile); }
	}
	
	public void sendInfo(Player player, String info) {
		player.sendMessage("[TitanChat] " + ChatColor.GOLD + info);
	}
	
	public void sendWarning(Player player, String warning) {
		player.sendMessage("[TitanChat] " + ChatColor.RED + warning);
	}
	
	public void setSilenced(boolean silenced) {
		this.silenced = silenced;
	}
	
	public boolean setupChatService() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		
		if (chatProvider != null)
			chat = chatProvider.getProvider();
		
		return chat != null;
	}
	
	public boolean setupPermissionService() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		
		if (permissionProvider != null)
			perm = permissionProvider.getProvider();
		
		return perm != null;
	}
	
	public boolean useDefaultFormat() {
		return getConfig().getBoolean("formatting.use-built-in");
	}
	
	public boolean usingVault() {
		return perm != null;
	}
	
	public void whitelistMember(Player player, Channel channel) {
		channel.getWhiteList().add(player.getName());
		channel.save();
		sendInfo(player, "You are now a Member of " + channel.getName());
	}
}