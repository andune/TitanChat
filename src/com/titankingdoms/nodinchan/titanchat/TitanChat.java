package com.titankingdoms.nodinchan.titanchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.titankingdoms.nodinchan.titanchat.TitanChatCommandHandler.Commands;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import de.bananaco.permissions.Permissions;

/*
 *     TitanChat 1.1
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
	
	private static Logger log = Logger.getLogger("TitanLog");
	
	private String defaultChannel = "";
	private String staffChannel = "";
	
	private File channelConfigFile = null;
	private FileConfiguration channelConfig = null;
	
	private boolean silence = false;
	
	private List<Player> local = new ArrayList<Player>();
	
	private List<String> channels = new ArrayList<String>();
	private List<String> globalChannels = new ArrayList<String>();

	private Map<Player, String> channel = new HashMap<Player, String>();
	private Map<String, List<OfflinePlayer>> channelAdmins = new HashMap<String, List<OfflinePlayer>>();
	private Map<String, List<OfflinePlayer>> channelMembers = new HashMap<String, List<OfflinePlayer>>();
	private Map<String, List<OfflinePlayer>> channelBans = new HashMap<String, List<OfflinePlayer>>();
	private Map<String, List<OfflinePlayer>> followers = new HashMap<String, List<OfflinePlayer>>();
	private Map<Player, List<String>> invitations = new HashMap<Player, List<String>>();
	private Map<String, List<Player>> invited = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> muted = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> participants = new HashMap<String, List<Player>>();
	private Map<String, Boolean> silenced = new HashMap<String, Boolean>();
	
	private Permission permission;
	private Chat chat;
	
	// Assigning an Admin to a channel
	
	public void assignAdmin(Player player, String channelName) {
		if (channelAdmins.isEmpty() || channelAdmins.get(channelName) == null) {
			List<OfflinePlayer> admins = new ArrayList<OfflinePlayer>();
			admins.add(player);
			channelAdmins.put(channelName, admins);
			
		} else {
			List<OfflinePlayer> admins = channelAdmins.get(channelName);
			admins.add(player);
			channelAdmins.put(channelName, admins);
		}
		
		sendInfo(player, "You are now an Admin of " + channelName);
	}
	
	// Banning a Member from a channel
	
	public void ban(Player player, String channelName) {
		if (isAdmin(player, channelName)) {
			List<OfflinePlayer> admins = new ArrayList<OfflinePlayer>();
			
			for (OfflinePlayer admin : channelAdmins.get(channelName)) {
				admins.add(admin);
			}
			
			admins.remove(player);
			
			if (admins.isEmpty()) {
				channelAdmins.remove(channelName);
				
			} else {
				channelAdmins.put(channelName, admins);
			}
			
		} else if (isMember(player, channelName)) {
			List<OfflinePlayer> members = new ArrayList<OfflinePlayer>();
			
			for (OfflinePlayer member : channelMembers.get(channelName)) {
				members.add(member);
			}
			
			members.remove(player);
			
			if (members.isEmpty()) {
				channelMembers.remove(channelName);
				
			} else {
				channelMembers.put(channelName, members);
			}
		}
		
		if (channelBans.get(channelName) == null) {
			List<OfflinePlayer> banned = new ArrayList<OfflinePlayer>();
			banned.add(player);
			channelBans.put(channelName, banned);
			
		} else {
			List<OfflinePlayer> banned = channelBans.get(channelName);
			banned.add(player);
			channelBans.put(channelName, banned);
		}
		
		channelSwitch(player, getChannel(player), getDefaultChannel());
		sendWarning(player, "You have been banned from " + channelName);
	}
	
	public boolean canAccess(Player player, String channelName) {
		if (channelName.equalsIgnoreCase("local"))
			return true;
		
		if (has(player, "TitanChat.access.*"))
			return true;
		
		if (has(player, "TitanChat.access." + channelName))
			return true;
		
		if (isPublic(channelName))
			return true;
		
		if (isAdmin(player, channelName))
			return true;
		
		if (isMember(player, channelName))
			return true;
		
		return false;
	}
	
	public boolean canBan(Player player, String channelName) {
		if (channelName.equalsIgnoreCase("local"))
			return false;
		
		if (has(player, "TitanChat.ban.*"))
			return true;
		
		if (has(player, "TitanChat.ban." + channelName))
			return true;
		
		if (isAdmin(player, channelName))
			return true;
		
		return false;
	}
	
	public boolean canKick(Player player, String channelName) {
		if (channelName.equalsIgnoreCase("local"))
			return false;
		
		if (has(player, "TitanChat.kick.*"))
			return true;
		
		if (has(player, "TitanChat.kick." + channelName))
			return true;
		
		if (isAdmin(player, channelName))
			return true;
		
		return false;
	}
	
	public boolean canMute(Player player, String channelName) {
		if (channelName.equalsIgnoreCase("local"))
			return false;
		
		if (has(player, "TitanChat.silence"))
			return true;
		
		if (has(player, "TitanChat.mute"))
			return true;
		
		if (isAdmin(player, channelName))
			return true;
		
		return false;
	}
	
	public boolean canRank(Player player, String channelName) {
		if (channelName.equalsIgnoreCase("local"))
			return false;
		
		if (has(player, "TitanChat.rank.*"))
			return true;
		
		if (has(player, "TitanChat.rank." + channelName))
			return true;
		
		if (isAdmin(player, channelName))
			return true;
		
		return false;
	}
	
	// Check if a channel exists
	
	public boolean channelExist(String channelName) {
		return getConfig().getConfigurationSection("channels").getKeys(false).contains(channelName);
	}
	
	// Switching channels
	
	public void channelSwitch(Player player, String oldCh, String newCh) {
		leaveChannel(player, oldCh);
		enterChannel(player, newCh);
	}
	
	// Check if the password is correct
	
	public boolean correctPass(String channelName, String password) {
		String pass = getConfig().getString("channels." + channelName + ".password");
		
		return pass.equals(password);
	}
	
	// Creating channels
	
	public void createChannel(Player player, String channelName) {
		assignAdmin(player, channelName);
		channelSwitch(player, getChannel(player), channelName);
		silenced.put(channelName, false);
		channels.add(channelName);
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
	
	// Deleting channels
	
	public void deleteChannel(Player player, String channelName) {
		if (getParticipants(channelName) != null) {
			List<Player> players = new ArrayList<Player>();
			
			for (Player participant : getParticipants(channelName)) {
				players.add(participant);
			}
			
			for (Player participant : players) {
				kick(participant, channelName);
				sendWarning(participant, channelName + " has been deleted");
			}
			
			participants.remove(channelName);
		}
		
		if (channelAdmins.get(channelName) != null) {
			channelAdmins.remove(channelName);
		}
		
		if (channelMembers.get(channelName) != null) {
			channelMembers.remove(channelName);
		}
		
		if (channelBans.get(channelName) != null) {
			channelBans.remove(channelName);
		}
		
		channels.remove(channelName);
	}
	
	// Demoting a player on a channel
	
	public void demote(Player player, String channelName) {
		List<OfflinePlayer> admins = new ArrayList<OfflinePlayer>();
		
		for (OfflinePlayer admin : channelAdmins.get(channelName)) {
			admins.add(admin);
		}
		
		admins.remove(player);
		
		if (admins.isEmpty()) {
			channelAdmins.remove(channelName);
			
		} else {
			channelAdmins.put(channelName, admins);
		}
		
		whitelistMember(player, channelName);
		sendInfo(player, "You have been demoted in " + channelName);
	}
	
	// Entering channels
	
	public void enterChannel(Player player, String channelName) {
		if (getParticipants(channelName) != null) {
			for (Player receiver : getParticipants(channelName)) {
				sendInfo(receiver, player.getDisplayName() + " has joined the channel");
			}
		}
		
		channel.put(player, channelName);
		
		if (participants.get(channelName) == null) {
			List<Player> players = new ArrayList<Player>();
			players.add(player);
			participants.put(channelName, players);
			
		} else {
			List<Player> players = participants.get(channelName);
			players.add(player);
			participants.put(channelName, players);
		}
	}
	
	// Entering local channel
	
	public void enterLocal(Player player) {
		local.add(player);
	}
	
	// Follows the channel
	
	public void follow(Player player, String channelName) {
		if (followers.get(channelName) == null) {
			List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
			players.add(player);
			followers.put(channelName, players);
			
		} else {
			List<OfflinePlayer> players = followers.get(channelName);
			players.add(player);
			followers.put(channelName, players);
		}
		
		sendInfo(player, "You have unfollowed " + channelName);
	}
	
	// Gets the channel the player is in
	
	public String getChannel(Player player) {
		return channel.get(player);
	}
	
	public int getChannelAmount() {
		return channels.size();
	}
	
	// Gets the player config of channels
	
	public FileConfiguration getChannelConfig() {
		if (channelConfig == null) {
			reloadChannelConfig();
		}
		
		return channelConfig;
	}
	
	// Gets the name of the default channel
	
	public String getDefaultChannel() {
		if (defaultChannel == "") {
			for (String channel : getConfig().getConfigurationSection("channels").getKeys(false)) {
				if (getConfig().get("channels." + channel + ".status") != null) {
					if (getConfig().getString("channels." + channel + ".status").equalsIgnoreCase("default")) {
						defaultChannel = channel;
					}
				}
			}
		}
		
		return defaultChannel;
	}
	
	// Gets the followers of a channel
	
	public List<Player> getFollowers(String channelName) {
		List<Player> followerList = new ArrayList<Player>();
		
		if (followers.get(channelName) != null) {
			for (OfflinePlayer player : followers.get(channelName)) {
				if (player.isOnline()) {
					followerList.add((Player) player);
				}
			}
		}
		
		return followerList;
	}
	
	// Gets the format of chat
	
	public String getFormat(String channelName) {
		if (getConfig().get("channels." + channelName + ".format") != null && !getConfig().getString("channels." + channelName + ".format").equalsIgnoreCase(""))
			
			return getConfig().getString("channels." + channelName + ".format");
		
		return getConfig().getString("formatting.format");
	}
	
	public OfflinePlayer getOfflinePlayer(String name) {
		return getServer().getOfflinePlayer(name);
	}
	
	// Gets the participants of a channel
	
	public List<Player> getParticipants(String channelName) {
		return participants.get(channelName);
	}
	
	public Player getPlayer(String name) {
		return getServer().getPlayer(name);
	}
	
	// Gets the prefix of the player
	
	public String getPrefix(Player player) {
		String prefix = "";
		
		if (vault() && chat != null) {
			prefix = chat.getPlayerPrefix(player);
			
		} else if (getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			
			if (user != null) {
				prefix = user.getPrefix();
			}
			
		} else if (getServer().getPluginManager().getPlugin("bPermissions") != null) {
			prefix = Permissions.getInfoReader().getPrefix(player);
		}
		
		return prefix;
	}
	
	// Gets the name of the staff channel
	
	public String getStaffChannel() {
		if (staffChannel.equals("")) {
			for (String channel : getConfig().getConfigurationSection("channels").getKeys(false)) {
				if (getConfig().get("channels." + channel + ".status") != null) {
					if (getConfig().getString("channels." + channel + ".status").equalsIgnoreCase("staff")) {
						staffChannel = channel;
					}
				}
			}
		}
		
		return staffChannel;
	}
	
	// Gets the list of status available for channels {
	
	public List<String> getStatuses() {
		List<String> statuses = new ArrayList<String>();
		
		for (Status status : Status.values()) {
			statuses.add(status.toString().toLowerCase());
		}
		
		return statuses;
	}
	
	// Gets the suffix of the player
	
	public String getSuffix(Player player) {
		String suffix = "";
		
		if (vault() && chat != null) {
			suffix = chat.getPlayerSuffix(player);
			
		} else if (getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			
			if (user != null) {
				suffix = user.getSuffix();
			}
			
		} else if (getServer().getPluginManager().getPlugin("bPermissions") != null) {
			suffix = Permissions.getInfoReader().getSuffix(player);
		}
		
		return suffix;
	}
	
	// Check for permission
	
	public boolean has(Player player, String permissionNode) {
		if (permission != null) {
			return permission.has(player, permissionNode);
		}
		
		return player.hasPermission(permissionNode);
	}
	
	// Check for Voice
	
	public boolean hasVoice(Player player) {
		return has(player, "TitanChat.voice");
	}
	
	public void infoLog(String info) {
		log.info("[" + this + "] " + info);
	}
	
	// Check if in local channel
	
	public boolean inLocal(Player player) {
		if (!local.isEmpty())
			return local.contains(player);
		
		return false;
	}
	
	// Inviting a player to join a channel
	
	public void invite(Player player, String channelName) {
		if (invited.isEmpty()) {
			List<Player> players = new ArrayList<Player>();
			players.add(player);
			invited.put(channelName, players);
			
		} else {
			List<Player> players = invited.get(channelName);
			players.add(player);
			invited.put(channelName, players);
		}
		
		if (invitations.isEmpty()) {
			List<String> channels = new ArrayList<String>();
			channels.add(channelName);
			invitations.put(player, channels);
			
		} else {
			List<String> channels = invitations.get(player);
			channels.add(channelName);
			invitations.put(player, channels);
		}
		
		sendInfo(player, "You have been invited to chat on " + channelName);
	}
	
	// Responding to invites
	
	public void inviteResponse(Player player, String channelName, boolean accept) {
		List<Player> players = new ArrayList<Player>();
		
		for (Player invitedPlayer : invited.get(channelName)) {
			players.add(invitedPlayer);
		}
		
		players.remove(player);
		
		if (players.isEmpty()) {
			invited.remove(channelName);
			
		} else {
			invited.put(channelName, players);
		}
		
		List<String> channels = new ArrayList<String>();
		
		for (String channelInvitation : invitations.get(player)) {
			channels.add(channelInvitation);
		}
		
		channels.remove(channelName);
		
		if (channels.isEmpty()) {
			invitations.remove(player);
			
		} else {
			invitations.put(player, channels);
		}
		
		if (accept) {
			String channel = getChannel(player);
			channelSwitch(player, channel, channelName);
		}
	}
	
	// Check if the player is an admin of that channel
	
	public boolean isAdmin(Player player, String channelName) {
		if (channelName.equalsIgnoreCase("local"))
			return false;
		
		if (isStaff(player))
			return true;
		
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		
		if (channelAdmins.get(channelName) != null) {
			players = channelAdmins.get(channelName);
		}
		
		if (players.contains(player))
			return true;
		
		return false;
	}
	
	// Check if the player is banned
	
	public boolean isBanned(Player player, String channelName) {
		if (channelBans.get(channelName) == null)
			return false;
		
		if (channelBans.isEmpty())
			return false;
		
		if (channelBans.get(channelName).isEmpty())
			return false;
		
		if (!channelBans.get(channelName).contains(player))
			return false;
		
		return true;
	}
	
	// Checks if the channel broadcasts to all channels
	
	public boolean isGlobal(String channelName) {
		return globalChannels.contains(channelName);
	}
	
	// Check if the player is invited
	
	public boolean isInvited(Player player, String channelName) {
		if (channelName.equalsIgnoreCase("local"))
			return false;
		
		if (invited.isEmpty())
			return false;
		
		if (invited.get(channelName).isEmpty())
			return false;
		
		if (invited.get(channelName).contains(player))
			return true;
		
		return false;
	}
	
	// Check if player is following
	
	public boolean isFollowing(Player player, String channelName) {
		if (followers.get(channelName) != null) {
			if (followers.get(channelName).contains(player))
				return true;
		}
		
		return false;
	}
	
	// Check if the channel is password protected
	
	public boolean isPassword(String channelName) {
		if (getStaffChannel().equals(channelName))
			return false;
		
		if (getDefaultChannel().equals(channelName))
			return false;
		
		if (getConfig().get("channels." + channelName + ".status") != null)
			return getConfig().getString("channels." + channelName + ".status").equalsIgnoreCase("password");
		
		return false;
	}
	
	// Check if the channel is private
	
	public boolean isPrivate(String channelName) {
		if (getStaffChannel().equals(channelName))
			return false;
		
		if (getDefaultChannel().equals(channelName))
			return true;
		
		if (getConfig().get("channels." + channelName + ".status") != null)
			return getConfig().getString("channels." + channelName + ".status").equalsIgnoreCase("private");
		
		return false;
	}
	
	// Check if the channel is public
	
	public boolean isPublic(String channelName) {
		if (getStaffChannel().equals(channelName))
			return false;
		
		if (getDefaultChannel().equals(channelName))
			return true;
		
		if (getConfig().get("channels." + channelName + ".status") != null)
			return getConfig().getString("channels." + channelName + ".status").equalsIgnoreCase("public");
		
		return false;
	}
	
	// Check if the player is a member of that channel
	
	public boolean isMember(Player player, String channelName) {
		if (channelName.equalsIgnoreCase("local"))
			return false;
		
		if (has(player, "TitanChat.access." + channelName))
			return true;
		
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		
		if (channelMembers.get(channelName) != null) {
			players = channelMembers.get(channelName);
			
			if (players.contains(player))
				return true;
		}
		
		return false;
	}
	
	// Check if the player is muted
	
	public boolean isMuted(Player player, String channelName) {
		if (muted.get(channelName) == null)
			return false;
		
		if (muted.get(channelName).contains(player))
			return true;
		
		return false;
	}
	
	// Check if the server is silenced
	
	public boolean isSilenced() {
		return silence;
	}
	
	// Check if the channel is silenced
	
	public boolean isSilenced(String channelName) {
		if (channelName.equals(getDefaultChannel()))
			return silence;
		
		if (channelName.equals(getStaffChannel()))
			return false;
		
		return silenced.get(channelName);
	}
	
	// Check if the player is staff
	
	public boolean isStaff(Player player) {
		if (has(player, "TitanChat.admin"))
			return true;
		
		return false;
	}
	
	// Kicks a player from a channel
	
	public void kick(Player player, String channelName) {
		channelSwitch(player, channelName, getDefaultChannel());
		sendWarning(player, "You have been kicked from " + channelName);
	}
	
	// Leaves the channel
	
	public void leaveChannel(Player player, String channelName) {
		channel.remove(player);
		List<Player> players = participants.get(channelName);
		players.remove(player);
		
		if (players.isEmpty()) {
			participants.remove(channelName);
			
		} else {
			participants.put(channelName, players);
		}
		
		if (getParticipants(channelName) != null) {
			for (Player receiver : getParticipants(channelName)) {
				sendInfo(receiver, player.getDisplayName() + " has left the channel");
			}
		}
	}
	
	public void leaveLocal(Player player) {
		local.remove(player);
	}
	
	// Joining or leaving local
	
	public void local(Player player, String channelName) {
		if (inLocal(player)) {
			leaveLocal(player);
			enterChannel(player, channelName);
			
		} else {
			leaveChannel(player, channelName);
			enterLocal(player);
		}
	}
	
	// Mutes the player
	
	public void mute(Player player, String channelName) {
		if (muted.get(channelName) == null) {
			List<Player> players = new ArrayList<Player>();
			players.add(player);
			muted.put(channelName, players);
			
		} else {
			List<Player> players = muted.get(channelName);
			players.add(player);
			muted.put(channelName, players);
		}
		sendWarning(player, "You have been muted on " + channelName);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			infoLog("Please use commands in-game");
			return true;
		}
		
		Player player = (Player) sender;
		
		// TitanChat Commands
		
		if (label.equalsIgnoreCase("titanchat") || label.equalsIgnoreCase("tc")) {
			if (args.length == 1) {
				
				// /titanchat allowcolours
				// Sets whether colour codes are allowed on the channel
				
				if (args[0].equalsIgnoreCase("allowcolours") || args[0].equalsIgnoreCase("allowcolors")) {
					if (has(player, "TitanChat.admin")) {
						new ConfigManager(this).setConvertColours(getChannel(player), (new Format(this).colours(getChannel(player))) ? false : true);
						sendInfo(player, "The channel now " + ((new Format(this).colours(getChannel(player))) ? "allows" : "disallows") + " colours");
						
					} else {
						sendWarning(player, "You do not have permission to change the state of this channel");
					}
					
					return true;
				}
				
				// /titanchat commands
				// The TitanChat Command Directory
				
				if (args[0].equalsIgnoreCase("commands")) {
					player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
					player.sendMessage(ChatColor.AQUA + "Command: /titanchat [action] [argument]");
					player.sendMessage(ChatColor.AQUA + "Alias: /tc action [argument]");
					player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					return true;
				}
				
				// /titanchat info
				// Gets the info on the channel
				
				if (args[0].equalsIgnoreCase("info")) {
					List<String> participants = new ArrayList<String>();
					List<String> followers = new ArrayList<String>();
					
					for (Player participant : getParticipants(getChannel(player))) {
						participants.add(participant.getName());
					}
					
					for (Player follower : getFollowers(getChannel(player))) {
						if (follower.isOnline()) {
							followers.add(follower.getName());
						}
					}
					
					String participantList = "";
					String followerList = "";
					
					if (participants.isEmpty())
						participantList = "None";
					else
						participantList = createList(participants);
					
					if (followers.isEmpty())
						followerList = "None";
					else
						followerList = createList(followers);
					
					player.sendMessage(ChatColor.AQUA + "Participants: " + participantList);
					player.sendMessage(ChatColor.AQUA + "Followers: " + followerList);
					return true;
				}
				
				// /titanchat list
				// Lists out the channels you have access to
				
				if (args[0].equalsIgnoreCase("list")) {
					List<String> channels = new ArrayList<String>();
					
					for (String channelName : getConfig().getConfigurationSection("channels").getKeys(false)) {
						if (canAccess(player, channelName)) {
							channels.add(channelName);
						}
					}
					
					if (channels.isEmpty())
						sendInfo(player, "Channel List: None");
					else
						sendInfo(player, "Channel List: " + createList(channels));
					
					return true;
				}
				
				// /titanchat reload
				// Reloads the configuration
				
				if (args[0].equalsIgnoreCase("reload")) {
					if (isStaff(player)) {
						infoLog("Reloading configs...");
						sendInfo(player, "Reloading configs...");
						saveConfig();
						saveChannelConfig();
						reloadConfig();
						reloadChannelConfig();
						prepareChannelCommunities();
						infoLog("Configs reloaded");
						sendInfo(player, "Configs reloaded");
					}
				}
				
				// /titanchat silence
				// Silences the whole server
				
				if (args[0].equalsIgnoreCase("silence")) {
					silence = (silence) ? false : true;
					
					for (Player receiver : getServer().getOnlinePlayers()) {
						if (silence) {
							sendWarning(receiver, "All channels have been silenced");
							
						} else {
							sendInfo(receiver, "Channels are no longer silenced");
						}
					}
					return true;
				}
				
				sendWarning(player, "Invalid Command/Argument");
				
			} else if (args.length == 2) {
				for (Commands command : Commands.values()) {
					if (command.toString().equalsIgnoreCase(args[0])) {
						if (getChannel(player) != null) {
							new TitanChatCommandHandler(this).onCommand(player, args[0], args[1], getChannel(player));
							
						} else {
							new TitanChatCommandHandler(this).onCommand(player, args[0], args[1], "local");
						}
						return true;
					}
				}
				
				sendWarning(player, "Invalid Command/Argument");
				
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("filter")) {
					for (Commands command : Commands.values()) {
						if (command.toString().equalsIgnoreCase(args[0])) {
							new TitanChatCommandHandler(this).onCommand(player, args[0], args[1], args[2]);
							return true;
						}
					}
					
				} else if (args[0].equalsIgnoreCase("commands")) {
					player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
					player.sendMessage(ChatColor.AQUA + "Command: /titanchat [action] [argument]");
					player.sendMessage(ChatColor.AQUA + "Alias: /tc action [argument]");
					player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					return true;
					
				} else {
					if (channelExist(args[2])) {
						for (Commands command : Commands.values()) {
							if (command.toString().equalsIgnoreCase(args[0])) {
								new TitanChatCommandHandler(this).onCommand(player, args[0], args[1], args[2]);
							}
						}
						
					} else {
						sendWarning(player, "Channel does not exist");
					}

					return true;
				}
				
				sendWarning(player, "Invalid Command/Arguments");
				
			} else {
				
				// /titanchat broadcast [message]
				// Broadcasts the message globally
				
				if (args[0].equalsIgnoreCase("broadcast")) {
					if (player.hasPermission("TitanChat.broadcast")) {
						StringBuilder str = new StringBuilder();
						
						for (int word = 1; word < args.length; word++) {
							if (str.length() > 0) {
								str.append(" ");
							}
							
							str.append(args[word]);
						}
						
						String msg = new Format(this).broadcast(player, new Format(this).filter(str.toString()));
						
						for (Player receiver : getServer().getOnlinePlayers()) {
							receiver.sendMessage(msg);
						}
						
						log.info("<" + player.getName() + "> " + str.toString());
						
					} else {
						sendWarning(player, "You do not have permission to broadcast");
					}

					return true;
				}
				
				// /titanchat commands [page]
				
				if (args[0].equalsIgnoreCase("commands")) {
					player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
					player.sendMessage(ChatColor.AQUA + "Command: /titanchat [action] [argument]");
					player.sendMessage(ChatColor.AQUA + "Alias: /tc action [argument]");
					player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					return true;
				}
				
				// /titanchat filter [phrase]
				
				if (args[0].equalsIgnoreCase("filter")) {
					if (isStaff(player)) {
						StringBuilder str = new StringBuilder();
						
						for (int word = 1; word < args.length; word++) {
							if (str.length() > 0) {
								str.append(" ");
							}
							
							str.append(args[word]);
						}
						
						new ConfigManager(this).filter(str.toString());
						sendInfo(player, "'" + str.toString() + "' has been filtered");
						
					} else {
						sendWarning(player, "You do not have permission to use this command");
					}

					return true;
				}
				
				// /titanchat format [format]
				// Sets the format of the channel
				
				if (args[0].equalsIgnoreCase("format")) {
					if (isAdmin(player, getChannel(player))) {
						StringBuilder str = new StringBuilder();
						
						for (int word = 1; word < args.length; word++) {
							if (str.length() > 0) {
								str.append(" ");
							}
							
							str.append(args[word]);
						}
						
						new ConfigManager(this).setFormat(getChannel(player), str.toString());
						sendInfo(player, "The format of " + getChannel(player) + " is now '" + str.toString() + "'");
						
					} else {
						sendWarning(player, "You do not have permission to use this command");
					}
					
					return true;
				}
				
				sendWarning(player, "Invalid Command/Argument");
			}
		}
		
		return false;
	}
	
	@Override
	public void onDisable() {
		infoLog("is now disabled");
	}
	
	@Override
	public void onEnable() {
		infoLog("is now enabling...");
		
		PluginManager pm = getServer().getPluginManager();
		
		if (getDefaultChannel().equals("")) {
			log.warning("[" + this + "] Default channel not defined");
			pm.disablePlugin(this);
		}
		
		if (vault()) {
			if (setupPermission()) {
				infoLog(permission.getName() + " detected");
				infoLog("Using " + permission.getName() + " for permissions");
			}
			
			if (setupChat()) {
				infoLog("Prefix and suffixes supported");
			}
		}
		
		File config = new File(getDataFolder() + "config.yml");
		File channelConfig = new File(getDataFolder() + "channels.yml");
		
		if (!config.exists()) {
			infoLog("Loading default config");
			getConfig().options().copyDefaults(true);
			getChannelConfig().options().copyHeader(true);
			saveConfig();
		}
		
		if (!channelConfig.exists()) {
			infoLog("Loading default channel players config");
			getChannelConfig().options().copyDefaults(true);
			getChannelConfig().options().copyHeader(true);
			saveChannelConfig();
		}
		
		prepareChannelCommunities();
		
		pm.registerEvents(new TitanChatPlayerListener(this), this);
		
		infoLog("is now enabled");
	}
	
	// Prepares the list of Admins and Members
	
	public void prepareChannelCommunities() {
		if (channelAdmins != null) {
			channelAdmins.clear();
		}
		
		if (channelMembers != null) {
			channelMembers.clear();
		}
		
		if (channelBans != null) {
			channelBans.clear();
		}
		
		if (followers != null) {
			followers.clear();
		}
		
		if (channels != null) {
			channels.clear();
		}
		
		if (globalChannels != null) {
			globalChannels.clear();
		}
		
		for (String channel : getChannelConfig().getConfigurationSection("channels").getKeys(false)) {
			List<String> adminNames = new ArrayList<String>();
			List<String> memberNames = new ArrayList<String>();
			List<String> bannedNames = new ArrayList<String>();
			List<String> followerNames = new ArrayList<String>();
			
			if (getChannelConfig().getStringList("channels." + channel + ".admins") != null) {
				adminNames = getChannelConfig().getStringList("channels." + channel + ".admins");
			}
			
			if (getChannelConfig().getStringList("channels." + channel + ".members") != null) {
				memberNames = getChannelConfig().getStringList("channels." + channel + ".members");
			}
			
			if (getChannelConfig().getStringList("channels." + channel + ".black-list") != null) {
				bannedNames = getChannelConfig().getStringList("channels." + channel + ".black-list");
			}
			
			if (getChannelConfig().getStringList("channels." + channel + ".followers") != null) {
				followerNames = getChannelConfig().getStringList("channels." + channel + ".followers");
			}
			
			List<OfflinePlayer> admins = new ArrayList<OfflinePlayer>();
			List<OfflinePlayer> members = new ArrayList<OfflinePlayer>();
			List<OfflinePlayer> banned = new ArrayList<OfflinePlayer>();
			List<OfflinePlayer> following = new ArrayList<OfflinePlayer>();
			
			if (!adminNames.isEmpty()) {
				for (String name : adminNames) {
					admins.add(getOfflinePlayer(name));
				}
				
				channelAdmins.put(channel, admins);
			}
			
			if (!memberNames.isEmpty()) {
				for (String name : memberNames) {
					members.add(getOfflinePlayer(name));
				}
				
				channelMembers.put(channel, members);
			}
			
			if (!bannedNames.isEmpty()) {
				for (String name : bannedNames) {
					banned.add(getOfflinePlayer(name));
				}
				
				channelBans.put(channel, banned);
			}
			
			if (!followerNames.isEmpty()) {
				for (String name : followerNames) {
					following.add(getOfflinePlayer(name));
				}
				
				followers.put(channel, following);
			}
			
			channels.add(channel);
			infoLog("No. of channels: " + channels.size());
			
			if (getConfig().get("channels." + channel + ".global") != null) {
				globalChannels.add(channel);
				infoLog("No. of global broadcasting channels: " + globalChannels.size());
			}
		}
		
		for (String channel : getConfig().getConfigurationSection("channels").getKeys(false)) {
			silenced.put(channel, false);
		}
		
		infoLog("Chat Communities Loaded");
	}
	
	// Promote a player on a channel
	
	public void promote(Player player, String channelName) {
		List<OfflinePlayer> members = new ArrayList<OfflinePlayer>();
		
		for (OfflinePlayer member : channelMembers.get(channelName)) {
			members.add(member);
		}
		
		members.remove(player);
		
		if (members.isEmpty()) {
			channelMembers.remove(channelName);
			
		} else {
			channelMembers.put(channelName, members);
		}
		
		assignAdmin(player, channelName);
		sendInfo(player, "You have been promoted in " + channelName);
	}
	
	// Reloads the player config of the channels
	
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
	
	// Saves the player config of the channels
	
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
	
	// Toggle between silencing for channels
	
	public void silence(String channelName) {
		boolean silence = (silenced.get(channelName)) ? false : true;
		silenced.put(channelName, silence);
	}
	
	// Unbans the player from the channel
	
	public void unban(Player player, String channelName) {
		List<OfflinePlayer> banned = new ArrayList<OfflinePlayer>();
		
		for (OfflinePlayer bannedPlayer : channelBans.get(channelName)) {
			banned.add(bannedPlayer);
		}
		
		banned.remove(player);
		
		if (banned.isEmpty()) {
			channelBans.remove(channelName);
			
		} else {
			channelBans.put(channelName, banned);
		}
		
		whitelistMember(player, channelName);
		
		sendInfo(player, "You have been unbanned from " + channelName);
	}
	
	// Unfollows the channel
	
	public void unfollow(Player player, String channelName) {
		if (followers.get(channelName) == null) {
			List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
			players.remove(player);
			followers.put(channelName, players);
			
		} else {
			List<OfflinePlayer> players = followers.get(channelName);
			players.remove(player);
			followers.put(channelName, players);
		}
		
		sendInfo(player, "You have unfollowed " + channelName);
	}
	
	// Unmutes the player
	
	public void unmute(Player player, String channelName) {
		List<Player> mutes = new ArrayList<Player>();
		
		for (Player mute : muted.get(channelName)) {
			mutes.add(mute);
		}
		
		mutes.remove(player);
		
		if (mutes.isEmpty()) {
			muted.remove(channelName);
			
		} else {
			muted.put(channelName, mutes);
		}
		
		sendInfo(player, "You have been unmuted on " + channelName);
	}
	
	// Checks whether or not to use the default format
	
	public boolean useDefaultFormat() {
		return getConfig().getBoolean("formatting.use-built-in");
	}
	
	public boolean vault() {
		return getServer().getPluginManager().getPlugin("Vault") != null;
	}
	
	// Whitelisting a Member on a channel
	
	public void whitelistMember(Player player, String channelName) {
		if (channelMembers.get(channelName) == null) {
			List<OfflinePlayer> members = new ArrayList<OfflinePlayer>();
			members.add(player);
			channelMembers.put(channelName, members);
			
		} else {
			List<OfflinePlayer> members = channelMembers.get(channelName);
			members.add(player);
			channelMembers.put(channelName, members);
		}
		
		sendInfo(player, "You are now a Member of " + channelName);
	}
	
	public enum Status {
		DEFAULT,
		PASSWORD,
		PRIVATE,
		PUBLIC,
		STAFF
	}
}
