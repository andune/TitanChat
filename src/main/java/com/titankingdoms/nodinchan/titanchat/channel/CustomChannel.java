package com.titankingdoms.nodinchan.titanchat.channel;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.addon.Addon;
import com.titankingdoms.nodinchan.titanchat.command.Command;

/**
 * CustomChannel - Custom Channel base
 * 
 * @author NodinChan
 *
 */
public class CustomChannel extends Channel {

	protected final TitanChat plugin;

	private Logger log = Logger.getLogger("TitanLog");
	
	public CustomChannel(String name) {
		super(name, Type.CUSTOM);
		this.plugin = TitanChat.getInstance();
	}
	
	public CustomChannel(String name, ChannelVariables variables) {
		super(name, variables);
		this.plugin = TitanChat.getInstance();
	}
	
	/**
	 * Colourise the line of text
	 * 
	 * @param text The text to be colourised
	 * 
	 * @return The colourised line of text
	 */
	public String colourise(String text) {
		return text.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	/**
	 * Decolourise the line of text
	 * 
	 * @param text The text to be decolourised
	 * 
	 * @return The decolourised line of text
	 */
	public String decolourise(String text) {
		return text.replaceAll("(&([a-f0-9A-F|kK]))", "");
	}
	
	/**
	 * The formatting of the channel
	 * 
	 * @param player The sender
	 * 
	 * @param message The message
	 * 
	 * @return The format
	 */
	public String format(Player player, String message) {
		return "<" + player.getDisplayName() + "> " + message;
	}
	
	/**
	 * Get the Logger with the given name
	 * 
	 * @param name The name of the Logger
	 * 
	 * @return The Logger
	 */
	public Logger getLogger(String name) {
		if (log.equals(Logger.getLogger("TitanLog"))) { log = Logger.getLogger(name); }
		return log;
	}
	
	/**
	 * Called when the Custom Channel is loaded by the Loader
	 */
	public void init() {}
	
	/**
	 * Load the variables
	 */
	public void loadVariables() {}
	
	/**
	 * Registers the addon
	 * 
	 * @param addon the addon to register
	 */
	public final void register(Addon addon) {
		plugin.getAddonManager().register(addon);
	}
	
	/**
	 * Registers the command
	 * 
	 * @param command the command to register
	 */
	public final void register(Command command) {
		plugin.getCommandManager().register(command);
	}
	
	/**
	 * Registers the custom channel
	 * 
	 * @param channel the channel to register
	 */
	public final void register(CustomChannel channel) {
		plugin.getChannelManager().register(channel);
	}
}