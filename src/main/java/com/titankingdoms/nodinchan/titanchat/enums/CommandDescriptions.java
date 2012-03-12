package com.titankingdoms.nodinchan.titanchat.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CommandDescriptions {
	ACCEPT("Accept", "Accepts the channel join invitation and joins the channel"),
	ADD("Add", "Whitelists the player for the channel"),
	BAN("Ban", "Bans the player from the channel"),
	BROADCAST("Broadcast", "Broadcasts the message globally"),
	COLOURCODES("ColourCodes", "Lists out avalable colour codes and respective colours"),
	COMMANDS("CommandDescriptions", "Shows the Command List"),
	CREATE("Create", "Creates a new channel"),
	DECLINE("Decline", "Declines the channel join invitation"),
	DELETE("Delete", "Deletes the channel"),
	DEMOTE("Demote", "Demotes the player of the channel"),
	EMOTE("Emote", "Action emote"),
	FOLLOW("Follow", "Follows the channel"),
	FORCE("Force", "Forces the player to join the channel"),
	INFO("Info", "Gets the participants and followers of the channel"),
	INVITE("Invite", "Invites the player to join the channel"),
	JOIN("Join", "Joins the channel"),
	KICK("Kick", "Kicks the player from the channel"),
	LIST("List", "Lists all channels you have access to"),
	MUTE("Mute", "Mutes the player on the channel"),
	PROMOTE("Promote", "Promotes the player of the channel"),
	RELOAD("Reload", "Reloads the config"),
	SET("Set", "Channel settings"),
	SILENCE("Silence", "Silences the channel/server"),
	TYPE("Type", "Sets the type of the channel"),
	UNBAN("Unban", "Unbans the player from the channel"),
	UNFOLLOW("Unfollow", "Unfollows the channel"),
	UNMUTE("Unmute", "Unmutes the player on the channel");
	
	private String name;
	private String description;
	
	private static Map<String, CommandDescriptions> NAME_MAP = new HashMap<String, CommandDescriptions>();
	private static Map<String, String> DESCRIPTION_MAP = new HashMap<String, String>();
	
	private CommandDescriptions(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	static {
		for (CommandDescriptions command : EnumSet.allOf(CommandDescriptions.class)) {
			NAME_MAP.put(command.name, command);
			DESCRIPTION_MAP.put(command.name, command.description);
		}
	}
	
	public static CommandDescriptions fromName(String name) {
		return NAME_MAP.get(name);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
}