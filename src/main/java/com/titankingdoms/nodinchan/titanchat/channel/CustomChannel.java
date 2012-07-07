package com.titankingdoms.nodinchan.titanchat.channel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.addon.Addon;
import com.titankingdoms.nodinchan.titanchat.command.CommandBase;
import com.titankingdoms.nodinchan.titanchat.event.MessageFormatEvent;
import com.titankingdoms.nodinchan.titanchat.event.MessageReceiveEvent;
import com.titankingdoms.nodinchan.titanchat.event.MessageSendEvent;
import com.titankingdoms.nodinchan.titanchat.event.MessageSendEvent.Message;

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

/**
 * CustomChannel - Custom Channel base
 * 
 * @author NodinChan
 *
 */
public class CustomChannel extends Channel implements Listener {

	protected final TitanChat plugin;

	private Logger log = Logger.getLogger("TitanLog");
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	public CustomChannel(String name) {
		super(name, Type.CUSTOM, Type.NONE);
		this.plugin = TitanChat.getInstance();
	}
	
	@Override
	public boolean canAccess(Player player) {
		return true;
	}
	
	/**
	 * Colourise the line of text
	 * 
	 * @param text The text to be colourised
	 * 
	 * @return The colourised line of text
	 */
	public String colourise(String text) {
		return plugin.getFormatHandler().colourise(text);
	}
	
	/**
	 * Decolourise the line of text
	 * 
	 * @param text The text to be decolourised
	 * 
	 * @return The decolourised line of text
	 */
	public String decolourise(String text) {
		return plugin.getFormatHandler().decolourise(text);
	}
	
	@Override
	public void deny(Player player, String message) {
		if (message != null && !message.equals(""))
			plugin.sendWarning(player, message);
		else
			plugin.sendWarning(player, "You do not have permission to join " + getName());
	}
	
	/**
	 * Formats the message
	 * 
	 * @param sender The sender of the message
	 * 
	 * @param format The message to format
	 * 
	 * @return The formatted message
	 */
	public String format(Player sender, String format) {
		return format.replace("%player", sender.getDisplayName());
	}
	
	/**
	 * Gets the config
	 * 
	 * @return The config
	 */
	public final FileConfiguration getConfig() {
		if (config == null)
			reloadConfig();
		
		return config;
	}
	
	/**
	 * Gets the format of the channel
	 * 
	 * @return The format of the channel
	 */
	public String getFormat() {
		return "<%player> %message";
	}
	
	/**
	 * Gets the Logger
	 * 
	 * @return The Logger
	 */
	public Logger getLogger() {
		return log;
	}
	
	/**
	 * Registers the addon
	 * 
	 * @param addon the addon to register
	 */
	public final void register(Addon addon) {
		plugin.getManager().getAddonManager().register(addon);
	}
	
	/**
	 * Registers the command
	 * 
	 * @param command the command to register
	 */
	public final void register(CommandBase command) {
		plugin.getManager().getCommandManager().register(command);
	}
	
	/**
	 * Registers the Listener
	 * 
	 * @param listener The Listener to register
	 */
	public final void register(Listener listener) {
		plugin.register(listener);
	}
	
	/**
	 * Called when TitanChat is reloaded
	 */
	public void reload() {}
	
	/**
	 * Reloads the config
	 */
	public final void reloadConfig() {
		if (configFile == null)
			configFile = new File(new File(plugin.getManager().getChannelManager().getCustomChannelDir(), super.getName()), "config.yml");
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = getResource("config.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	/**
	 * Saves the config
	 */
	public final void saveConfig() {
		if (config == null || configFile == null) { return; }
		try { config.save(configFile); } catch (IOException e) {}
	}
	
	@Override
	protected final String sendMessage(Player sender, List<Player> recipants, String message) {
		MessageFormatEvent formatEvent = new MessageFormatEvent(sender, format(sender, getFormat()));
		plugin.getServer().getPluginManager().callEvent(formatEvent);
		
		MessageSendEvent sendEvent = new MessageSendEvent(sender, this, recipants, new Message(formatEvent.getFormat(), message));
		plugin.getServer().getPluginManager().callEvent(sendEvent);
		
		if (sendEvent.isCancelled()) { return ""; }
		
		MessageReceiveEvent receiveEvent = new MessageReceiveEvent(sendEvent.getSender(), sendEvent.getRecipants(), new Message(sendEvent.getFormat(), sendEvent.getMessage()));
		plugin.getServer().getPluginManager().callEvent(receiveEvent);
		
		for (Player recipant : receiveEvent.getRecipants()) {
			String[] lines = plugin.getFormatHandler().regroup(receiveEvent.getFormat(recipant), receiveEvent.getMessage(recipant));
			
			recipant.sendMessage(receiveEvent.getFormat(recipant).replace("%message", lines[0]));
			recipant.sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
		}
		
		return sendEvent.getFormat().replace("%message", sendEvent.getMessage());
	}
	
	@Override
	protected final String sendMessage(Player sender, Player[] recipants, String message) {
		return sendMessage(sender, Arrays.asList(recipants), message);
	}
	
	@Override
	public final void setSpecialType(Type type) {}
	
	@Override
	public final void setSpecialType(String type) {
		setSpecialType(Type.fromName(type));
	}
	
	@Override
	public final void setType(Type type) {}
	
	@Override
	public final void setType(String type) {
		setType(Type.fromName(type));
	}
}