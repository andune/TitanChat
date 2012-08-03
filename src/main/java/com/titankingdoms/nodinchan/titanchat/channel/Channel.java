package com.titankingdoms.nodinchan.titanchat.channel;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.nodinchan.ncbukkit.loader.Loadable;
import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.util.handler.CommandHandler;
import com.titankingdoms.nodinchan.titanchat.channel.util.handler.Handler;
import com.titankingdoms.nodinchan.titanchat.channel.util.handler.SettingHandler;
import com.titankingdoms.nodinchan.titanchat.channel.util.Info;
import com.titankingdoms.nodinchan.titanchat.channel.util.Participant;
import com.titankingdoms.nodinchan.titanchat.event.channel.MessageConsoleEvent;
import com.titankingdoms.nodinchan.titanchat.event.channel.MessageReceiveEvent;
import com.titankingdoms.nodinchan.titanchat.event.channel.MessageSendEvent;
import com.titankingdoms.nodinchan.titanchat.event.util.Message;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
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

public abstract class Channel extends Loadable {
	
	protected final TitanChat plugin;
	
	private Option option;
	
	private final Info info;
	
	private final List<String> admins;
	private final List<String> blacklist;
	private final List<String> followers;
	private final List<String> whitelist;
	
	private final Map<String, Participant> participants;
	
	private final Handler handler;
	
	private String password;
	
	private File configFile;
	private FileConfiguration config;
	
	public Channel() {
		this("", Option.TYPE);
	}
	
	public Channel(String name, Option option) {
		super(name);
		this.plugin = TitanChat.getInstance();
		this.option = option;
		this.info = new Info(this);
		this.admins = new ArrayList<String>();
		this.blacklist = new ArrayList<String>();
		this.followers = new ArrayList<String>();
		this.whitelist = new ArrayList<String>();
		this.participants = new HashMap<String, Participant>();
		this.handler = new Handler();
	}
	
	public abstract boolean access(Player player);
	
	public final boolean changeSetting(CommandSender sender, String setting, String[] args) {
		return handler.changeSetting(sender, setting, args);
	}
	
	public abstract Channel create(CommandSender sender, String name, Option option);
	
	public void deny(Player player, String message) {
		if (message != null && !message.equals(""))
			plugin.send(MessageLevel.WARNING, player, message);
		else
			plugin.send(MessageLevel.WARNING, player, "You do not have access");
	}
	
	public final List<String> getAdmins() {
		return admins;
	}
	
	public final List<String> getBlacklist() {
		return blacklist;
	}
	
	@Override
	public FileConfiguration getConfig() {
		if (config == null)
			reloadConfig();
		
		return config;
	}
	
	public final List<String> getFollowers() {
		return followers;
	}
	
	public Info getInfo() {
		return info;
	}
	
	public final Option getOption() {
		return option;
	}
	
	public final List<Participant> getParticipants() {
		return new ArrayList<Participant>(participants.values());
	}
	
	public String getPassword() {
		return password;
	}
	
	public abstract String getType();
	
	public final List<String> getWhitelist() {
		return whitelist;
	}
	
	public final boolean handleCommand(CommandSender sender, String command, String[] args) {
		return handler.handleCommand(sender, command, args);
	}
	
	public boolean isParticipating(String name) {
		return participants.containsKey(name.toLowerCase());
	}
	
	public void join(String name) {
		if (!participants.containsKey(name.toLowerCase()) && plugin.getManager().getChannelManager().getParticipant(name) != null)
			participants.put(name.toLowerCase(), plugin.getManager().getChannelManager().getParticipant(name)).join(this);
	}
	
	public void join(Player player) {
		join(player.getName());
	}
	
	public void leave(String name) {
		if (participants.containsKey(name.toLowerCase()))
			participants.remove(name.toLowerCase()).leave(this);
	}
	
	public void leave(Player player) {
		leave(player.getName());
	}
	
	public abstract Channel load(String name, Option option);
	
	@Override
	public void reloadConfig() {
		if (configFile == null)
			configFile = new File(plugin.getChannelDir(), getName() + ".yml");
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = plugin.getResource("channel.yml");
		
		if (defConfigStream != null)
			config.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
	}
	
	public final void registerCommandHandlers(CommandHandler... handlers) {
		handler.registerCommandHandlers(handlers);
	}
	
	public final void registerSettingHandlers(SettingHandler... handlers) {
		handler.registerSettingHandlers(handlers);
	}
	
	public void save() {
		getConfig().set("admins", admins);
		getConfig().set("blacklist", blacklist);
		getConfig().set("whitelist", whitelist);
		getConfig().set("followers", followers);
		saveConfig();
	}
	
	@Override
	public void saveConfig() {
		if (configFile == null || config == null)
			return;
		
		try { config.save(configFile); } catch (Exception e) { plugin.log(Level.SEVERE, "Could not save config to " + configFile); }
	}
	
	public final void saveParticipants() {
		List<String> participants = new ArrayList<String>();
		
		for (Participant participant : getParticipants())
			participants.add(participant.getName());
		
		getConfig().set("participants", participants);
		saveConfig();
	}
	
	public void send(String message) {
		String[] lines = plugin.getFormatHandler().regroup("", message);
		
		for (Participant participant : getParticipants()) {
			if (participant.getPlayer() != null) {
				participant.getPlayer().sendMessage(lines[0]);
				participant.getPlayer().sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
			}
		}
	}
	
	public void send(String... messages) {
		for (String message : messages)
			send(message);
	}
	
	public String sendMessage(Player sender, String message) {
		return sendMessage(sender, new ArrayList<Player>(), message);
	}
	
	protected final String sendMessage(Player sender, List<Player> recipants, String message) {
		return sendMessage(sender, recipants.toArray(new Player[0]), message);
	}
	
	protected final String sendMessage(Player sender, Player[] recipants, String message) {
		String format = plugin.getFormatHandler().format(sender, getName());
		
		MessageSendEvent sendEvent = new MessageSendEvent(sender, this, recipants, new Message(format, message));
		plugin.getServer().getPluginManager().callEvent(sendEvent);
		
		if (sendEvent.isCancelled())
			return "";
		
		MessageReceiveEvent receiveEvent = new MessageReceiveEvent(sender, sendEvent.getRecipants(), new Message(sendEvent.getFormat(), sendEvent.getMessage()));
		plugin.getServer().getPluginManager().callEvent(receiveEvent);
		
		for (Player recipant : receiveEvent.getRecipants()) {
			String[] lines = plugin.getFormatHandler().regroup(receiveEvent.getFormat(recipant), receiveEvent.getMessage(recipant));
			
			recipant.sendMessage(receiveEvent.getFormat(recipant).replace("%message", lines[0]));
			recipant.sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
		}
		
		MessageConsoleEvent consoleEvent = new MessageConsoleEvent(sender, new Message(sendEvent.getFormat(), sendEvent.getMessage()));
		plugin.getServer().getPluginManager().callEvent(consoleEvent);
		
		return consoleEvent.getFormat().replace("%message", consoleEvent.getMessage());
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Option - Option of the channel
	 * 
	 * @author NodinChan
	 *
	 */
	public enum Option {
		CUSTOM("custom"),
		DEFAULT("default"),
		NONE("none"),
		STAFF("staff"),
		TYPE("type");
		
		private String name;
		private static Map<String, Option> NAME_MAP = new HashMap<String, Option>();
		
		private Option(String name) {
			this.name = name;
		}
		
		static {
			for (Option option : EnumSet.allOf(Option.class))
				NAME_MAP.put(option.name.toLowerCase(), option);
		}
		
		public static Option fromName(String name) {
			return NAME_MAP.get(name.toLowerCase());
		}
		
		public String getName() {
			return name;
		}
	}
	
	/**
	 * Range - Range to send the message
	 * 
	 * @author NodinChan
	 *
	 */
	public enum Range {
		CHANNEL("channel"),
		GLOBAL("global"),
		LOCAL("local"),
		WORLD("world");
		
		private String name;
		private static Map<String, Range> NAME_MAP = new HashMap<String, Range>();
		
		private Range(String name) {
			this.name = name;
		}
		
		static {
			for (Range range : EnumSet.allOf(Range.class))
				NAME_MAP.put(range.name.toLowerCase(), range);
		}
		
		public static Range fromName(String name) {
			return NAME_MAP.get(name.toLowerCase());
		}
		
		public String getName() {
			return name;
		}
	}
}