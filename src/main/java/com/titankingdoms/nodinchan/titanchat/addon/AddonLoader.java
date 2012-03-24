package com.titankingdoms.nodinchan.titanchat.addon;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.nodinchan.loader.LoadEvent;
import com.nodinchan.loader.Loader;
import com.titankingdoms.nodinchan.titanchat.TitanChat;

public final class AddonLoader extends Loader<Addon> {
	
	private final AddonManager manager;
	
	public AddonLoader(TitanChat plugin) {
		super(plugin, plugin.getAddonDir(), new Object[0]);
		this.manager = plugin.getAddonManager();
		register(this);
	}
	
	@Override
	public Logger getLogger() {
		return TitanChat.getInstance().getLogger();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLoad(LoadEvent event) {
		if (event.getLoadable() instanceof Addon && event.getPlugin() instanceof TitanChat)
			manager.setJarFile((Addon) event.getLoadable(), event.getJarFile());
	}
}