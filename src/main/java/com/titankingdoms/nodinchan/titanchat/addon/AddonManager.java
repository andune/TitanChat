package com.titankingdoms.nodinchan.titanchat.addon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

/**
 * AddonManager - Manages Addons and executes them when needed
 * 
 * @author NodinChan
 *
 */
public final class AddonManager {
	
	private final TitanChat plugin;
	
	private static final Debugger db = new Debugger(2);
	
	private final List<Addon> addons;
	
	public AddonManager(TitanChat plugin) {
		this.plugin = plugin;
		this.addons = new ArrayList<Addon>();
	}
	
	/**
	 * Tells all Addons that a chat message has been sent
	 * 
	 * @param name The name of the player
	 * 
	 * @param message The message sent
	 */
	public void chatMade(String name, String message) {
		for (Addon addon : addons) { addon.chatMade(name, message); }
	}
	
	/**
	 * Tells all Addons that a chat message will be sent
	 * 
	 * @param player The message sender
	 * 
	 * @param message The message
	 * 
	 * @return The formatted message
	 */
	public String executeFormat(Player player, String message) {
		for (Addon addon : addons) { message = addon.format(player, message); }
		return message;
	}
	
	/**
	 * Gets the Addon by its name
	 * 
	 * @param name The name of the Addon
	 * 
	 * @return The Addon if it exists, otherwise null
	 */
	public Addon getAddon(String name) {
		for (Addon addon : addons) {
			if (addon.getName().equalsIgnoreCase(name))
				return addon;
		}
		
		return null;
	}
	
	/**
	 * Loads the Addons
	 */
	public void load() {
		try { for (Addon addon : plugin.getLoader().loadAddons()) { register(addon); } } catch (Exception e) {}
		sortAddons();
	}
	
	/**
	 * Registers the Addon
	 * 
	 * @param addon The Addon to register
	 */
	public void register(Addon addon) {
		db.i("Registering addon: " + addon.getName());
		addons.add(addon);
	}
	
	/**
	 * Sorts the Addons
	 */
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
	
	/**
	 * Unloads the Addons
	 */
	public void unload() {
		addons.clear();
	}
}