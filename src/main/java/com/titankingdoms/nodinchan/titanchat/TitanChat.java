package com.titankingdoms.nodinchan.titanchat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.titankingdoms.nodinchan.titanchat.addon.AddonManager;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.CommandManager;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;
import com.titankingdoms.nodinchan.titanchat.permissions.PermissionsHook;
import com.titankingdoms.nodinchan.titanchat.util.Format;
import com.titankingdoms.nodinchan.titanchat.util.Loader;

/*
 *     TitanChat 3.0
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

public final class TitanChat extends JavaPlugin {
	
	private static final Logger log = Logger.getLogger("TitanLog");
	private static final Debugger db = new Debugger(1);
	
	private AddonManager addonManager;
	private ChannelManager chManager;
	private CommandManager cmdManager;
	private Format format;
	private PermissionsHook permHook;
	private Loader loader;
	
	private boolean silenced = false;
	
	private List<String> muted;
	
	private Permission perm;
	private Chat chat;
	
	public void assignAdmin(Player player, Channel channel) {
		db.i("Assigning player " + player.getName() +
				" as admin of channel " + channel.getName());
		
		channel.getAdminList().add(player.getName());
		channel.save();
		sendInfo(player, "You are now an Admin of " + channel.getName());
	}
	
	public void channelSwitch(Player player, Channel oldCh, Channel newCh) {
		db.i("Channel switch of player " + player.getName() +
				" from channel " + oldCh.getName() +
				" to channel " + newCh.getName());
		
		oldCh.leave(player);
		newCh.join(player);
	}
	
	public String createList(List<String> list) {
		db.i("Creating string out of stringlist: " + list.toString());
		
		StringBuilder str = new StringBuilder();
		
		for (String item : list) {
			if (str.length() > 0)
				str.append(", ");
			
			str.append(item);
		}
		
		return str.toString();
	}
	
	public boolean enableChannels() {
		return getConfig().getBoolean("channels.enable-channels");
	}
	
	public boolean enableJoinMessage() {
		return getConfig().getBoolean("channels.messages.join");
	}
	
	public boolean enableLeaveMessage() {
		return getConfig().getBoolean("channels.messages.leave");
	}
	
	public File getAddonDir() {
		return new File(getDataFolder(), "addons");
	}
	
	public AddonManager getAddonManager() {
		return addonManager;
	}
	
	public File getChannelDir() {
		return new File(getDataFolder(), "channels");
	}
	
	public ChannelManager getChannelManager() {
		return chManager;
	}
	
	public File getCommandDir() {
		return new File(getAddonDir(), "commands");
	}
	
	public CommandManager getCommandManager() {
		return cmdManager;
	}
	
	public File getCustomChannelDir() {
		return new File(getAddonDir(), "channels");
	}

	public Format getFormat() {
		return format;
	}
	
	public String getGroupPrefix(Player player) {
		db.i("Getting group prefix of player " + player.getName());
		
		if (chat != null) {
			String prefix = chat.getGroupPrefix(player.getWorld(), perm.getPrimaryGroup(player));
			db.i("Returning: " + prefix);
			return (prefix != null) ? prefix : "";
		}

		db.i("Returning PermissionsHook group prefix");
		return permHook.getGroupPrefix(player);
	}
	
	public String getGroupSuffix(Player player) {
		db.i("Getting group suffix of player " + player.getName());
		
		if (chat != null) {
			String suffix = chat.getGroupSuffix(player.getWorld(), perm.getPrimaryGroup(player));
			db.i("Returning: " + suffix);
			return (suffix != null) ? suffix : "";
		}
		
		db.i("Returning PermissionsHook group suffix");
		return permHook.getGroupSuffix(player);
	}
	
	public Loader getLoader() {
		return loader;
	}
	
	public Player getPlayer(String name) {
		return getServer().getPlayer(name);
	}
	
	public String getPlayerPrefix(Player player) {
		db.i("Getting prefix of player: " + player.getName());
		
		if (chat != null) {
			String prefix = chat.getPlayerPrefix(player.getWorld(), player.getName());
			db.i("Returning: " + prefix);
			return (prefix != null) ? prefix : "";
		}
		
		db.i("Returning PermissionsHook player prefix");
		return permHook.getPlayerPrefix(player);
	}
	
	public String getPlayerSuffix(Player player) {
		db.i("Getting suffix of player: " + player.getName());
		
		if (chat != null) {
			String suffix = chat.getPlayerSuffix(player.getWorld(), player.getName());
			db.i("Returning: " + suffix);
			return (suffix != null) ? suffix : "";
		}
		
		db.i("Returning PermissionsHook player suffix");
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
	
	public void mute(Player player, boolean mute) {
		db.i((mute ? "" : "un") + "muting player");
		
		if (mute)
			muted.add(player.getName());
		else
			muted.remove(player.getName());
	}
	
	public boolean muted(Player player) {
		return muted.contains(player.getName());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		db.i("onCommand: " + cmd.getName());
		
		if (cmd.getName().equals("titanchat")) {
			if (args.length < 1) {
				db.i("onCommand: No arguments!");
				
				sender.sendMessage(ChatColor.AQUA + "TitanChat Commands");
				sender.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
				sender.sendMessage(ChatColor.AQUA + "Alias: /tc [command] [arguments]");
				
				if (sender instanceof Player)
					sendInfo((Player) sender, "'/titanchat commands [page]' for command list");
				else
					log(Level.INFO, "'/titanchat commands [page]' for command list");
				
				return true;
			}
			
			if (!(sender instanceof Player)) {
				if (args[0].equalsIgnoreCase("reload")) {
					log(Level.INFO, "Reloading configs...");
					reloadConfig();
					chManager.reload();
					log(Level.INFO, "Configs reloaded");
					return true;
				}
				
				log(Level.INFO, "Please use commands in-game"); return true;
			}

			db.i("CommandManager executing command:");
			cmdManager.execute((Player) sender, args[0], parseCommand(args));
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("broadcast")) {
			if (!(sender instanceof Player)) {
				if (!getConfig().getBoolean("broadcast.server.enable")) {
					log(Level.WARNING, "Command disabled");
					return true;
				}
				
				String message = getConfig().getString("broadcast.server.format");
				
				StringBuilder str = new StringBuilder();
				
				for (String word : args) {
					if (str.length() > 0)
						str.append(" ");
					
					str.append(word);
				}
				
				message = message.replace("%message", str.toString());
				
				getServer().broadcastMessage(getFormat().colourise(message));
				getLogger().info("<Server> " + getFormat().decolourise(str.toString()));
				return true;
			}
			
			if (!getConfig().getBoolean("broadcast.player.enable")) {
				sendWarning((Player) sender, "Command disabled");
				return true;
			}
			
			if (has((Player) sender, "TitanChat.broadcast"))
				try { cmdManager.execute((Player) sender, "broadcast", args); } catch (Exception e) {}
			else
				sendWarning((Player) sender, "You do not have permission");
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("me")) {
			if (!(sender instanceof Player)) {
				if (!getConfig().getBoolean("emote.server.enable")) {
					log(Level.WARNING, "Command disabled");
					return true;
				}
				
				String message = getConfig().getString("emote.server.format");
				
				StringBuilder str = new StringBuilder();
				
				for (String word : args) {
					if (str.length() > 0)
						str.append(" ");
					
					str.append(word);
				}
				
				message = message.replace("%action", str.toString());
				getServer().broadcastMessage(getFormat().colourise(message));
				getLogger().info("* Server " + getFormat().decolourise(str.toString()));
				return true;
			}
			
			if (!getConfig().getBoolean("emote.player.enable")) {
				sendWarning((Player) sender, "Command disabled");
				return true;
			}
			
			if (has((Player) sender, "TitanChat.emote.server"))
				try { cmdManager.execute((Player) sender, "me", args); } catch (Exception e) {}
			else
				sendWarning((Player) sender, "You do not have permission");
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onDisable() {
		log(Level.INFO, "is now disabling...");
		
		log(Level.INFO, "Unloading managers...");

		addonManager.unload();
		chManager.unload();
		cmdManager.unload();
		
		log(Level.INFO, "is now disabled");
	}
	
	@Override
	public void onEnable() {
		log(Level.INFO, "is now enabling...");
		
		muted = new ArrayList<String>();
		
		addonManager = new AddonManager(this);
		chManager = new ChannelManager(this);
		cmdManager = new CommandManager(this);
		format = new Format(this);
		permHook = new PermissionsHook(this);
		
		File config = new File(getDataFolder(), "config.yml");
		
		if (!config.exists()) {
			log(Level.INFO, "Loading default config");
			saveResource("config.yml", false);
		}
		
		if (getAddonDir().mkdir())
			log(Level.INFO, "Creating addon directory...");
		
		if (getCustomChannelDir().mkdir())
			log(Level.INFO, "Creating custom channel directory...");
		
		if (getCommandDir().mkdir())
			log(Level.INFO, "Creating commands directory");
		
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
		
		Debugger.load(this);
		
		if (pm.getPlugin("Vault") != null) {
			setupChatService();
			setupPermissionService();
		}
		
		pm.registerEvents(permHook, this);
		pm.registerEvents(new TitanChatListener(this), this);
		
		try { chManager.load(); } catch (Exception e) {}
		
		addonManager.load();
		cmdManager.load();
		
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
	
	public String[] parseCommand(String[] args) {
		db.i("Parsing command");
		StringBuilder str = new StringBuilder();
		
		for (String arg : args) {
			if (str.length() > 0)
				str.append(" ");
			
			if (arg.equals(args[0]))
				continue;
			
			str.append(arg);
		}
		db.i("Command arguments: " + str.toString());
		
		return (str.toString().equals("")) ? new String[] {} : str.toString().split(" ");
	}
	
	public void sendInfo(Player player, String info) {
		db.i("@" + player.getName() + ": " + info);
		
		player.sendMessage("[TitanChat] " + ChatColor.GOLD + info);
	}
	
	public void sendInfo(List<String> players, String info) {
		for (String player : players) {
			if (getPlayer(player) != null)
				sendInfo(getPlayer(player), info);
		}
	}
	
	public void sendWarning(Player player, String warning) {
		db.i("Warning @" + player.getName() + ": " + warning);
		
		player.sendMessage("[TitanChat] " + ChatColor.RED + warning);
	}
	
	public void sendWarning(List<String> players, String warning) {
		for (String player : players) {
			if (getPlayer(player) != null)
				sendWarning(getPlayer(player), warning);
		}
	}
	
	public void setSilenced(boolean silenced) {
		db.i("Setting silenced to " + silenced);
		this.silenced = silenced;
	}
	
	public boolean setupChatService() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		
		if (chatProvider != null)
			chat = chatProvider.getProvider();

		db.i("Vault Chat Service is set up: " + (chat != null));
		return chat != null;
	}
	
	public boolean setupPermissionService() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		
		if (permissionProvider != null)
			perm = permissionProvider.getProvider();
		
		db.i("Vault Permission Service is set up: " + (perm != null));
		return perm != null;
	}
	
	public boolean useDefaultFormat() {
		return getConfig().getBoolean("formatting.use-built-in");
	}
	
	public boolean usingVault() {
		db.i("Using Vault: " + (perm != null));
		return perm != null;
	}
	
	public void whitelistMember(Player player, Channel channel) {
		db.i("Adding player " + player.getName() +
				" to whitelist of channel " + channel.getName());
		channel.getWhiteList().add(player.getName());
		channel.save();
		sendInfo(player, "You are now a Member of " + channel.getName());
	}
}