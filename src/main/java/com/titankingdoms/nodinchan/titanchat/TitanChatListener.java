package com.titankingdoms.nodinchan.titanchat;

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
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
 * TitanChatListener - Event Listener of TitanChat
 * 
 * @author NodinChan
 *
 */
public class TitanChatListener implements Listener {

	private final TitanChat plugin;
	
	private final double currentVer;
	private double newVer;
	
	/**
	 * Listens to events and act accordingly
	 */
	public TitanChatListener() {
		this.plugin = TitanChat.getInstance();
		this.currentVer = Double.valueOf(plugin.getDescription().getVersion().trim().split(" ")[0].trim());
		updateCheck();
	}
	
	/**
	 * Listens to the PlayerChatEvent
	 * 
	 * @param event PlayerChatEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		event.setCancelled(true);
		
		if (plugin.enableChannels()) {
			String quickMessage = plugin.getConfig().getString("channels.quick-message");
			
			if (message.startsWith(quickMessage) && !message.substring(quickMessage.length()).startsWith(" ")) {
				String channel = message.split(" ")[0].substring(quickMessage.length());
				String msg = message.substring(message.split(" ")[0].length());
				plugin.getServer().dispatchCommand(player, "titanchat send " + channel + msg);
				return;
			}
			
			Channel channel = plugin.getManager().getChannelManager().getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "You are not in a channel, please join one to chat");
				return;
			}
			
			if (voiceless(player, channel))
				return;
			
			String log = channel.sendMessage(player, message);
			
			if (!log.equals(""))
				plugin.getServer().getConsoleSender().sendMessage(log);
			
		} else {
			String format = plugin.getFormatHandler().format(player, null, true);
			
			MessageSendEvent sendEvent = new MessageSendEvent(player, null, plugin.getServer().getOnlinePlayers(), new Message(format, message));
			plugin.getServer().getPluginManager().callEvent(sendEvent);
			
			if (sendEvent.isCancelled())
				return;
			
			String log = format.replace("%message", plugin.getFormatHandler().colourise(sendEvent.getMessage()));
			
			for (Player recipant : plugin.getServer().getOnlinePlayers())
				recipant.sendMessage(log);
			
			if (!log.equals(""))
				plugin.getServer().getConsoleSender().sendMessage(log);
		}
	}
	
	/**
	 * Listens to the PlayerJoinEvent
	 * 
	 * @param event PlayerJoinEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getDisplayNameChanger().apply(event.getPlayer());
		
		if (plugin.getPermsBridge().has(event.getPlayer(), "TitanChat.update")) {
			if (newVer > currentVer) {
				String message = ChatColor.GOLD + "%new" + ChatColor.DARK_PURPLE + " is out! You are running " + ChatColor.GOLD + "%current";
				event.getPlayer().sendMessage(message.replace("%new", newVer + "").replace("%current", currentVer + ""));
				event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Update at " + ChatColor.BLUE + "http://dev.bukkit.org/server-mods/titanchat/");
			}
		}
		
		if (!plugin.enableChannels())
			return;
		
		if (plugin.getManager().getChannelManager().getChannel(event.getPlayer()) != null)
			return;
		
		Channel channel = plugin.getManager().getChannelManager().getSpawnChannel(event.getPlayer());
		
		if (channel != null)
			channel.join(event.getPlayer());
		else
			plugin.sendWarning(event.getPlayer(), "Failed to find your spawn channel");
		
		if (plugin.isSilenced())
			plugin.sendWarning(event.getPlayer(), "All channels are silenced");
		else if (channel != null && channel.isSilenced())
			plugin.sendWarning(event.getPlayer(), channel.getName() + " is silenced");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		for (int line = 0; line < 4; line++)
			event.setLine(line, plugin.getFormatHandler().colourise(event.getLine(line)));
	}
	
	/**
	 * Checks for an update
	 */
	protected void updateCheck() {
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/titanchat/files.rss");
			
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			
			Node node = doc.getElementsByTagName("item").item(0);
			
			if (node.getNodeType() == 1) {
				Element element = (Element) node;
				Element name = (Element) element.getElementsByTagName("title").item(0);
				this.newVer = Double.valueOf(name.getChildNodes().item(0).getNodeValue().split(" ")[1].trim().substring(1));
			}
			
		} catch (Exception e) {}
		
		this.newVer = Double.valueOf(plugin.getDescription().getVersion().trim().split(" ")[0].trim());
	}
	
	private boolean voiceless(Player player, Channel channel) {
		if (plugin.getPermsBridge().has(player, "TitanChat.voice"))
			return false;
		
		if (plugin.isSilenced()) {
			plugin.sendWarning(player, "The server is silenced");
			return true;
		}
		
		if (channel.isSilenced()) {
			plugin.sendWarning(player, "The channel is silenced");
			return true;
		}
		
		if (channel.getMuteList().contains(player.getName())) {
			plugin.sendWarning(player, "You have been muted");
			return true;
		}
		
		if (plugin.getManager().getChannelManager().isMuted(player)) {
			plugin.sendWarning(player, "You have been muted");
			return true;
		}
		
		return false;
	}
}