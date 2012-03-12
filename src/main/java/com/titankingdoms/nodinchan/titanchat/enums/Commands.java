package com.titankingdoms.nodinchan.titanchat.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.command.commands.*;

public enum Commands {
	ACCEPT("Accept", AcceptCommand.class, new String[] { "accept" }, "accept [channel]"),
	ADD("Add", AddCommand.class, new String[] { "add" }, "add [player] <channel>"),
	BAN("Ban", BanCommand.class, new String[] { "ban" }, "ban [player] <channel>"),
	BROADCAST("Broadcast", BroadcastCommand.class, new String[] { "broadcast" }, "broadcast [message]"),
	COLOURCODES("ColourCodes", ColourCodesCommand.class, new String[] { "colourcodes", "colorcodes", "colours", "colors", "codes" }, "colourcodes"),
	COMMANDS("Commands", CommandsCommand.class, new String[] { "commands" }, "commands <page/command>"),
	CREATE("Create", CreateCommand.class, new String[] { "create" }, "create [channel]"),
	DECLINE("Decline", DeclineCommand.class, new String[] { "decline" }, "decline [channel]"),
	DELETE("Delete", DeleteCommand.class, new String[] { "delete" }, "delete [channel]"),
	DEMOTE("Demote", DemoteCommand.class, new String[] { "demote" }, "demote [player] <channel>"),
	EMOTE("Emote", EmoteCommand.class, new String[] { "me", "em" }, "me [action]"),
	FOLLOW("Follow", FollowCommand.class, new String[] { "follow" }, "follow [channel]"),
	FORCE("Force", ForceCommand.class, new String[] { "force" }, "force [player] <channel>"),
	INFO("Info", InfoCommand.class, new String[] { "info" }, "info <channel>"),
	INVITE("Invite", InviteCommand.class, new String[] { "invite" }, "invite [player] <channel>"),
	JOIN("Join", JoinCommand.class, new String[] { "join" }, "join [channel] <password>"),
	KICK("Kick", KickCommand.class, new String[] { "kick" }, "kick [player] <channel>"),
	LIST("List", ListCommand.class, new String[] { "list" }, "list"),
	MUTE("Mute", MuteCommand.class, new String[] { "mute" }, "mute [player] <channel>"),
	PROMOTE("Promote", PromoteCommand.class, new String[] { "promote" }, "promote [player] <channel>"),
	RELOAD("Reload", ReloadCommand.class, new String[] { "reload" }, "reload"),
	SET("Set", SetCommand.class, new String[] { "set" }, "set [variable] <value> <channel>"),
	SILENCE("Silence", SilenceCommand.class, new String[] { "silence" }, "silence <channel>"),
	UNBAN("Unban", UnbanCommand.class, new String[] { "unban" }, "unban [player] <channel>"),
	UNFOLLOW("Unfollow", UnfollowCommand.class, new String[] { "unfollow" }, "unfollow [channel]"),
	UNMUTE("Unmute", UnmuteCommand.class, new String[] { "unmute" }, "unmute [player] <channel>");
	
	private String name;
	private Class<? extends Command> command;
	private String[] aliases;
	private String usage;
	
	private static Map<String, Commands> NAME_MAP = new HashMap<String, Commands>();
	private static Map<String, String> USAGE_MAP = new HashMap<String, String>();
	
	private Commands(String name, Class<? extends Command> command, String[] aliases, String usage) {
		this.name = name;
		this.command = command;
		this.aliases = aliases;
		this.usage = usage;
	}
	
	static {
		for (Commands command : EnumSet.allOf(Commands.class)) {
			for (String name : command.aliases) {
				NAME_MAP.put(name, command);
				USAGE_MAP.put(name, command.usage);
			}
		}
	}
	
	public static Commands fromName(String name) {
		return NAME_MAP.get(name);
	}
	
	public String[] getAliases() {
		return aliases;
	}
	
	public Class<? extends Command> getCommand() {
		return command;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return CommandDescriptions.fromName(name).getDescription();
	}
	
	public String getUsage() {
		return usage;
	}
	
	public void invalidArgLength(Player player) {
		player.sendMessage("[TitanChat] " + ChatColor.RED + "Invalid Argument Length");
		player.sendMessage("[TitanChat] " + ChatColor.GOLD + "Usage: " + usage);
	}
}