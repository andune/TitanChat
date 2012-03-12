package com.titankingdoms.nodinchan.titanchat.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum Settings {
	CHCOLOUR("ChColour", new String[] { "chcolour", "chcolor" }, "Changes the chat display colour of the channel", "chcolour [colourcode] <channel>"),
	CONVERT("Convert", new String[] { "convert" }, "Toggles colour code converting", "convert <channel>"),
	NCOLOUR("NColour", new String[] { "ncolour", "ncolor" }, "Changes the name display colour of the channel", "ncolour [colourcode] <channel>"),
	PASSWORD("Password", new String[] { "password" }, "Sets the password of the channel", "password [password] <channel>"),
	TAG("Tag", new String[] { "tag" }, "Sets the tag of the channel", "tag [tag] <channel>"),
	TYPE("Type", new String[] { "type" }, "Sets the type of the channel", "type [type] <channel>");

	private String description;
	private String name;
	private String[] names;
	private String usage;
	
	private static Map<String, Settings> NAME_MAP = new HashMap<String, Settings>();
	private static Map<String, String> DESCRIPTION_MAP = new HashMap<String, String>();
	private static Map<String, String> USAGE_MAP = new HashMap<String, String>();
	
	private Settings(String name, String[] names, String description, String usage) {
		this.name = name;
		this.names = names;
		this.description = description;
		this.usage = usage;
	}
	
	static {
		for (Settings setting : EnumSet.allOf(Settings.class)) {
			for (String name : setting.names) {
				NAME_MAP.put(name, setting);
				DESCRIPTION_MAP.put(name, setting.description);
				USAGE_MAP.put(name, setting.usage);
			}
		}
	}
	
	public static Settings fromName(String name) {
		return NAME_MAP.get(name);
	}
	
	public String[] getAliases() {
		return names;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public void invalidArgLength(Player player) {
		player.sendMessage("[TitanChat] " + ChatColor.RED + "Invalid Argument Length");
		player.sendMessage("[TitanChat] " + ChatColor.GOLD + "Usage: " + usage);
	}
}