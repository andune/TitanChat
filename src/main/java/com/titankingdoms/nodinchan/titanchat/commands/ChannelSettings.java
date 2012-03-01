package com.titankingdoms.nodinchan.titanchat.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Type;
import com.titankingdoms.nodinchan.titanchat.util.ConfigManager;
import com.titankingdoms.nodinchan.titanchat.util.Format;

public class ChannelSettings {
	
	private TitanChat plugin;
	private ConfigManager cfgManager;
	
	public ChannelSettings(TitanChat plugin) {
		this.plugin = plugin;
		this.cfgManager = new ConfigManager(plugin);
	}
	
	public void channelColour(Player player, String colourCode, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getAdminList().contains(player.getName())) {
				cfgManager.setChannelColour(channelName, colourCode);
				plugin.sendInfo(player, "You have changed the colour to " + colourCode);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the channel colour");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void convertColour(Player player, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.has(player, "TitanChat.admin")) {
				cfgManager.setConvertColours(channelName, (new Format(plugin).colours(channelName)) ? false : true);
				plugin.sendInfo(player, "The channel now " + ((new Format(plugin).colours(channelName)) ? "converts" : "ignores") + " colour codes");
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change this setting of this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void follow(Player player, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canAccess(player)) {
				if (plugin.getChannel(channelName).isFollowing(player)) {
					plugin.sendWarning(player, "You are already following " + channelName);
					
				} else {
					Channel channel = plugin.getChannel(channelName);
					channel.getFollowers().add(player.getName());
					plugin.sendInfo(player, "You have followed " + channel.getName());
					
					cfgManager.follow(player, channelName);
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to follow that channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void format(Player player, String format, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getAdminList().contains(player.getName())) {
				cfgManager.setFormat(channelName, format);
				plugin.sendInfo(player, "The format of " + channelName + " is now '" + format + "'");
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the format of the channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void nameColour(Player player, String colourCode, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getAdminList().contains(player.getName())) {
				cfgManager.setNameColour(channelName, colourCode);
				plugin.sendInfo(player, "You have changed the colour to " + colourCode);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the name colour");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void password(Player player, String password, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getAdminList().contains(player.getName())) {
				Channel channel = plugin.getChannel(channelName);
				channel.setPassword(password);
				
				cfgManager.setPassword(channelName, password);
				plugin.sendInfo(player, "You have changed the password of " + channel.getName() + " to " + password);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the password of this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void tag(Player player, String tag, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getAdminList().contains(player.getName())) {
				cfgManager.setTag(channelName, tag);
				plugin.sendInfo(player, "You have changed the settings");
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the tag on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void type(Player player, String type, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
				if (Type.fromName(type) != null) {
					switch (Type.fromName(type)) {
					
					case CUSTOM:
						plugin.sendInfo(player, "You cannot set a channel's type as custom");
						break;
					
					case DEFAULT:
					case STAFF:
						if (plugin.isStaff(player)) {
							Channel channel = plugin.getChannel(channelName);
							channel.setType(type);
							
							cfgManager.setType(channelName, Type.fromName(type).getName());
							plugin.sendInfo(player, "The channel is now " + Type.fromName(type).getName());
							
						} else {
							plugin.sendWarning(player, "You do not have permission to set the channel as this type");
						}
						break;
						
					case PASSWORD:
					case PRIVATE:
					case PUBLIC:
						Channel channel = plugin.getChannel(channelName);
						channel.setType(type);
						
						cfgManager.setType(channelName, Type.fromName(type).getName());
						plugin.sendInfo(player, "The channel is now " + Type.fromName(type).getName());
						break;
					}
					
				} else {
					plugin.sendWarning(player, "Type does not exist");
					
					StringBuilder str = new StringBuilder();
					
					for (Type typeEnum : Type.values()) {
						if (str.length() > 0)
							str.append(", ");
						
						str.append(typeEnum.getName());
					}
					
					plugin.sendInfo(player, "Available types: " + str.toString());
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the type of this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void unfollow(Player player, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).isFollowing(player)) {
				Channel channel = plugin.getChannel(channelName);
				channel.getFollowers().remove(player.getName());
				plugin.sendInfo(player, "You have unfollowed " + channel.getName());
				
				cfgManager.unfollow(player, channelName);
				
			} else {
				plugin.sendWarning(player, "You are not following " + channelName);
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
}