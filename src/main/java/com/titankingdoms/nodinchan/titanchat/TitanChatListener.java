package com.titankingdoms.nodinchan.titanchat;

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

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

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.util.Participant;
import com.titankingdoms.nodinchan.titanchat.event.chat.MessageSendEvent;

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
	
	private long chars = 0;
	private long lines = 0;
	private long words = 0;
	
	private final double currentVer;
	private double newVer;
	
	/**
	 * Listens to events and act accordingly
	 */
	public TitanChatListener() {
		this.plugin = TitanChat.getInstance();
		this.currentVer = Double.valueOf(plugin.getDescription().getVersion().trim().split(" ")[0].trim());
		this.newVer = updateCheck();
	}
	
	public long getCharacters() {
		return chars;
	}
	
	public long getLines() {
		return lines;
	}
	
	public long getWords() {
		return words;
	}
	
	/**
	 * Listens to the MessageSendEvent
	 * 
	 * @param event MessageSendEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMessageSend(MessageSendEvent event) {
		this.chars += event.getMessage().toCharArray().length;
		this.words += event.getMessage().split(" ").length;
		this.lines++;
	}
	
	/**
	 * Listens to the PlayerChatEvent
	 * 
	 * @param event PlayerChatEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event) {
		event.setCancelled(true);
		
		ChatPacket packet = new ChatPacket(event.getPlayer(), event.getMessage());
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, packet);
	}
	
	/**
	 * Listens to the PlayerJoinEvent
	 * 
	 * @param event PlayerJoinEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Participant participant = plugin.getManager().getChannelManager().loadParticipant(event.getPlayer());
		plugin.getDisplayNameChanger().apply(participant.getPlayer());
		updateCheck(participant.getPlayer());
	}
	
	/**
	 * Listens to the SignChangeEvent
	 * 
	 * @param event SignChangeEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		for (int line = 0; line < 4; line++)
			event.setLine(line, plugin.getFormatHandler().colourise(event.getLine(line)));
	}
	
	/**
	 * Checks for an update
	 */
	private double updateCheck() {
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/titanchat/files.rss");
			
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
			doc.getDocumentElement().normalize();
			
			Node node = doc.getElementsByTagName("item").item(0);
			
			if (node.getNodeType() == 1) {
				Node name = ((Element) node).getElementsByTagName("title").item(0);
				Node version = name.getChildNodes().item(0);
				this.newVer = Double.valueOf(version.getNodeValue().split(" ")[1].trim().substring(1));
			}
			
		} catch (Exception e) { this.newVer = currentVer; }
		
		return this.newVer;
	}
	
	public void updateCheck(Player player) {
		if (updateCheck() <= currentVer || !plugin.getPermissionsHandler().has(player, "TitanChat.update"))
			return;
		
		player.sendMessage(plugin.getFormatHandler().colourise("&6" + newVer + " &5is out! You are running &6" + currentVer));
		player.sendMessage(plugin.getFormatHandler().colourise("&5Update at &9http://dev.bukkit.org/server-mods/titanchat"));
	}
	
	/**
	 * ChatPacket - Created when chat has to be processed
	 * 
	 * @author NodinChan
	 *
	 */
	public final class ChatPacket implements Runnable {
		
		private final TitanChat plugin;
		
		private final Player sender;
		
		private final String message;
		
		public ChatPacket(Player sender, String message) {
			this.plugin = TitanChat.getInstance();
			this.sender = sender;
			this.message = message;
		}
		
		public void run() {
			Channel channel = plugin.getManager().getChannelManager().getChannel(sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "You are not in a channel, please join one to chat");
				return;
			}
			
			if (plugin.voiceless(sender, channel, true))
				return;
			
			String log = channel.sendMessage(sender, message);
			
			if (log != null && !log.equals(""))
				plugin.chatLog(log);
		}
	}
}