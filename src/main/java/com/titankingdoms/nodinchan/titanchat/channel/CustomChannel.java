package com.titankingdoms.nodinchan.titanchat.channel;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.addon.Addon;
import com.titankingdoms.nodinchan.titanchat.command.Command;

public class CustomChannel extends Channel {

	protected final TitanChat plugin;
	
	private Logger log = Logger.getLogger("TitanLog");
	
	public CustomChannel(TitanChat plugin, String name) {
		super(plugin, name, Type.CUSTOM);
		this.plugin = plugin;
	}
	
	public CustomChannel(TitanChat plugin, String name, ChannelVariables variables) {
		super(plugin, name, variables);
		this.plugin = plugin;
	}
	
	public String colourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	public String decolourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "");
	}
	
	public String format(Player player, String message) {
		return "<" + player.getDisplayName() + "> " + message;
	}
	
	public Logger getLogger(String name) {
		if (log.equals(Logger.getLogger("TitanLog"))) { log = Logger.getLogger(name); }
		return log;
	}
	
	public void init() {}
	
	public void loadVariables() {}
	
	public final void register(Addon addon) {
		plugin.getAddonManager().register(addon);
	}
	
	public final void register(CustomChannel channel) {
		plugin.getChannelManager().register(channel);
	}
	
	public final void register(Command command) {
		plugin.getCommandManager().register(command);
	}
}