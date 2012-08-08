package com.titankingdoms.nodinchan.titanchat.processing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public final class ChatProcessor extends Thread implements Listener {
	
	private final TitanChat plugin;
	
	private final Queue<ChatPacket> chatQueue;
	
	public ChatProcessor() {
		this.plugin = TitanChat.getInstance();
		this.chatQueue = new ConcurrentLinkedQueue<ChatPacket>();
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Listens to the AsyncPlayerChatEvent
	 * 
	 * @param event AsyncPlayerChatEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		
		ChatProcess process = new ChatProcess(event.getPlayer(), event.getMessage());
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, process);
	}
	
	@Override
	public void run() {
		while (true) {
			while (!chatQueue.isEmpty()) {
				synchronized (chatQueue) {
					chatQueue.poll().chat();
				}
			}
		}
	}
	
	public void sendPacket(ChatPacket packet) {
		if (packet != null) {
			synchronized (chatQueue) {
				chatQueue.offer(packet);
			}
		}
	}
}