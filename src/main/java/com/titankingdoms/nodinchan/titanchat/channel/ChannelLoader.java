package com.titankingdoms.nodinchan.titanchat.channel;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.nodinchan.loader.LoadEvent;
import com.nodinchan.loader.Loader;
import com.titankingdoms.nodinchan.titanchat.TitanChat;

public final class ChannelLoader extends Loader<CustomChannel> {
	
	private final ChannelManager manager;
	
	public ChannelLoader(TitanChat plugin) {
		super(plugin, plugin.getCustomChannelDir(),new Object[0]);
		this.manager = plugin.getChannelManager();
		register(this);
	}
	
	@Override
	public Logger getLogger() {
		return TitanChat.getInstance().getLogger();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLoad(LoadEvent event) {
		if (event.getLoadable() instanceof CustomChannel && event.getPlugin() instanceof TitanChat)
			manager.setJarFile((CustomChannel) event.getLoadable(), event.getJarFile());
	}
}