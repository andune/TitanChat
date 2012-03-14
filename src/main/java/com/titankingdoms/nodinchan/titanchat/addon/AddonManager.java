package com.titankingdoms.nodinchan.titanchat.addon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

public final class AddonManager {
	
	private final TitanChat plugin;
	
	private static final Debugger db = new Debugger(2);
	
	private final List<Addon> addons;
	
	public AddonManager(TitanChat plugin) {
		this.plugin = plugin;
		this.addons = new ArrayList<Addon>();
	}
	
	public void chatMade(String name, String message) {
		for (Addon addon : addons) { addon.chatMade(name, message); }
	}
	
	public String executeFormat(Player player, String message) {
		for (Addon addon : addons) { return addon.format(player, message); }
		return message;
	}
	
	public Addon getAddon(String name) {
		for (Addon addon : addons) {
			if (addon.getName().equalsIgnoreCase(name))
				return addon;
		}
		
		return null;
	}
	
	public void load() {
		try { for (Addon addon : plugin.getLoader().loadAddons()) { register(addon); } } catch (Exception e) {}
		sortAddons();
	}
	
	public void register(Addon addon) {
		db.i("Registering addon: " + addon.getName());
		addons.add(addon);
	}
	
	public void sortAddons() {
		List<Addon> addons = new ArrayList<Addon>();
		List<String> names = new ArrayList<String>();
		
		for (Addon addon : this.addons) {
			names.add(addon.getName());
		}
		
		Collections.sort(names);
		
		for (String name : names) {
			addons.add(getAddon(name));
		}
		
		this.addons.clear();
		this.addons.addAll(addons);
	}
	
	public void unload() {
		addons.clear();
	}
}