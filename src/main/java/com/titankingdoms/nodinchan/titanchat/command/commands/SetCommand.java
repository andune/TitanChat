package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;
import com.titankingdoms.nodinchan.titanchat.enums.Settings;
import com.titankingdoms.nodinchan.titanchat.enums.Type;

public class SetCommand extends Command {
	
	public SetCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.SET, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); return; }
		if (Settings.fromName(args[0]) == null) { plugin.sendWarning(player, "Invalid Setting"); return; }
		
		switch (Settings.fromName(args[0])) {
		
		case CHCOLOUR:
			if (args.length < 2) { Settings.CHCOLOUR.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						cm.getChannel(args[2]).getVariables().setChatColour(args[1]);
						cm.getChannel(args[2]).save();
						
						plugin.sendInfo(player, "You have changed the colour to " + args[1]);
						
					} else {
						plugin.sendWarning(player, "You do not have permission");
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					cm.getChannel(player).getVariables().setChatColour(args[1]);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "You have changed the colour to " + args[1]);
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
			}
			break;
			
		case CONVERT:
			try {
				if (cm.exists(args[1])) {
					if (plugin.isStaff(player)) {
						cm.getChannel(args[1]).getVariables().setConvert((cm.getChannel(args[1]).getVariables().convert()) ? false : true);
						cm.getChannel(args[1]).save();
						
						plugin.sendInfo(player, "The channel now " + ((cm.getChannel(args[1]).getVariables().convert()) ? "converts" : "ignores") + " colour codes");
						
					} else {
						plugin.sendWarning(player, "You do not have permission");
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} catch (IndexOutOfBoundsException e) {
				if (plugin.isStaff(player)) {
					cm.getChannel(player).getVariables().setConvert((cm.getChannel(player).getVariables().convert()) ? false : true);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "The channel now " + ((cm.getChannel(player).getVariables().convert()) ? "converts" : "ignores") + " colour codes");
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
			}
			break;
			
		case NCOLOUR:
			if (args.length < 2) { Settings.NCOLOUR.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						cm.getChannel(args[2]).getVariables().setNameColour(args[1]);
						cm.getChannel(args[2]).save();
						
						plugin.sendInfo(player, "You have changed the colour to " + args[1]);
						
					} else {
						plugin.sendWarning(player, "You do not have permission");
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					cm.getChannel(player).getVariables().setNameColour(args[1]);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "You have changed the colour to " + args[1]);
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
			}
			break;
			
		case PASSWORD:
			if (args.length < 2) { Settings.PASSWORD.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						cm.getChannel(args[2]).setPassword(args[1]);
						cm.getChannel(args[2]).save();
						
						plugin.sendInfo(player, "You have changed the password of " + cm.getChannel(player).getName() + " to " + args[1]);
						
					} else {
						plugin.sendWarning(player, "You do not have permission");
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					cm.getChannel(player).setPassword(args[1]);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "You have changed the password of " + cm.getChannel(player).getName() + " to " + args[1]);
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
			}
			break;
			
		case TAG:
			if (args.length < 2) { Settings.TAG.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						cm.getChannel(args[2]).getVariables().setTag(args[1]);
						cm.getChannel(args[2]).save();
						
						plugin.sendInfo(player, "You have changed the settings");
						
					} else {
						plugin.sendWarning(player, "You do not have permission");
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					cm.getChannel(player).getVariables().setTag(args[1]);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "You have changed the settings");
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
			}
			break;
			
		case TYPE:
			if (args.length < 2) { Settings.TYPE.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						if (Type.fromName(args[1]) != null) {
							switch (Type.fromName(args[1])) {
							
							case CUSTOM:
								plugin.sendInfo(player, "You cannot set a channel's type as custom");
								break;
							
							case DEFAULT:
							case STAFF:
								if (plugin.isStaff(player)) {
									Channel channel = new Channel(cm.getChannel(args[2]), Type.fromName(args[1]));
									plugin.getChannelManager().getChannels().remove(cm.getChannel(args[2]));
									plugin.getChannelManager().getChannels().add(channel);
									channel.save();
									
									plugin.sendInfo(player, "The channel is now " + Type.fromName(args[1]).getName());
									
								} else {
									plugin.sendWarning(player, "You do not have permission");
								}
								break;
								
							case PASSWORD:
							case PRIVATE:
							case PUBLIC:
								Channel channel = new Channel(cm.getChannel(args[2]), Type.fromName(args[1]));
								plugin.getChannelManager().getChannels().remove(cm.getChannel(args[1]));
								plugin.getChannelManager().getChannels().add(channel);
								channel.save();
								
								plugin.sendInfo(player, "The channel is now " + Type.fromName(args[1]).getName());
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
						plugin.sendWarning(player, "You do not have permission");
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					if (Type.fromName(args[1]) != null) {
						switch (Type.fromName(args[1])) {
						
						case CUSTOM:
							plugin.sendInfo(player, "You cannot set a channel's type as custom");
							break;
						
						case DEFAULT:
						case STAFF:
							if (plugin.isStaff(player)) {
								Channel oldType = cm.getChannel(player);
								Channel channel = new Channel(oldType, Type.fromName(args[1]));
								plugin.getChannelManager().getChannels().remove(oldType);
								plugin.getChannelManager().getChannels().add(channel);
								channel.save();
								
								plugin.sendInfo(player, "The channel is now " + Type.fromName(args[1]).getName());
								
							} else {
								plugin.sendWarning(player, "You do not have permission");
							}
							break;
							
						case PASSWORD:
						case PRIVATE:
						case PUBLIC:
							Channel oldType = cm.getChannel(player);
							Channel channel = new Channel(oldType, Type.fromName(args[1]));
							plugin.getChannelManager().getChannels().remove(oldType);
							plugin.getChannelManager().getChannels().add(channel);
							channel.save();
							
							plugin.sendInfo(player, "The channel is now " + Type.fromName(args[1]).getName());
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
					plugin.sendWarning(player, "You do not have permission");
				}
			}
			break;
		}
	}
}