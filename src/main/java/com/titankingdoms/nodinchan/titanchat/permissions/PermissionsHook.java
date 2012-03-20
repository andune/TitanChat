package com.titankingdoms.nodinchan.titanchat.permissions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

/**
 * PermissionsHook - For hooking into permissions plugins
 * 
 * @author NodinChan
 *
 */
public final class PermissionsHook implements Listener {
	
	private final TitanChat plugin;
	
	private final WildcardNodes wildcard;
	
	private static final Debugger db = new Debugger(5);
	
	private Plugin permissionsPlugin;
	
	private String name = "SuperPerms";
	
	public PermissionsHook(TitanChat plugin) {
		this.plugin = plugin;
		this.wildcard = new WildcardNodes(this);
	}
	
	/**
	 * Check if a package exists
	 * 
	 * @param packages The file path
	 * 
	 * @return True if the package exists
	 */
	public boolean exists(String pkg) {
		try { Class.forName(pkg); return true; } catch (Exception e) { return false; }
	}
	
	/**
	 * Gets the group prefix of the Player
	 * 
	 * @param player The Player to check
	 * 
	 * @return The group prefix of the Player
	 */
	public String getGroupPrefix(Player player) {
		String prefix = "";
		
		switch (using()) {
		
		case PERMISSIONSEX:
			String[] pexGroups = PermissionsEx.getPermissionManager().getUser(player).getGroupsNames();
			String pexGroup = (pexGroups != null && pexGroups.length > 0) ? pexGroups[0] : "";
			prefix = PermissionsEx.getPermissionManager().getGroup(pexGroup).getPrefix(player.getWorld().getName());
			db.i("PermissionsEx returned group prefix: " + prefix);
			break;
			
		case BPERMISSIONS:
			String[] bGroups = ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
			String bGroup = (bGroups != null && bGroups.length > 0) ? bGroups[0] : "";
			prefix = (!bGroup.equals("")) ? ApiLayer.getValue(player.getWorld().getName(), CalculableType.GROUP, bGroup, "prefix") : "";
			db.i("bPermissions returned group prefix: " + prefix);
			break;
			
		case GROUPMANAGER:
			String group = ((GroupManager) permissionsPlugin).getWorldsHolder().getWorldPermissions(player).getGroup(player.getName());
			prefix = ((GroupManager) permissionsPlugin).getWorldsHolder().getWorldPermissions(player).getGroupPrefix(group);
			db.i("GroupManager returned group prefix: " + prefix);
			break;
		}
		
		if (prefix.equals("") || prefix == null) {
			db.i("Permissions plugins did not return any prefix, checking permissions...");
			prefix = wildcard.getGroupPrefix(player);
		}
		
		return (prefix.equals("") || prefix == null) ? "" : prefix;
	}
	
	/**
	 * Gets the group suffix of the Player
	 * 
	 * @param player The Player to check
	 * 
	 * @return The group suffix of the Player
	 */
	public String getGroupSuffix(Player player) {
		String suffix = "";
		
		switch (using()) {
		
		case PERMISSIONSEX:
			String[] pexGroups = PermissionsEx.getPermissionManager().getUser(player).getGroupsNames();
			String pexGroup = (pexGroups != null && pexGroups.length > 0) ? pexGroups[0] : "";
			suffix = (!pexGroup.equals("")) ? PermissionsEx.getPermissionManager().getGroup(pexGroup).getSuffix(player.getWorld().getName()) : "";
			db.i("PermissionsEx returned group suffix: " + suffix);
			break;
			
		case BPERMISSIONS:
			String[] bGroups = ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
			String bGroup = (bGroups != null && bGroups.length > 0) ? bGroups[0] : "";
			suffix = (!bGroup.equals("")) ? ApiLayer.getValue(player.getWorld().getName(), CalculableType.GROUP, bGroup, "suffix") : "";
			db.i("bPermissions returned group suffix: " + suffix);
			break;
			
		case GROUPMANAGER:
			String gmGroup = ((GroupManager) permissionsPlugin).getWorldsHolder().getWorldPermissions(player).getGroup(player.getName());
			suffix = ((GroupManager) permissionsPlugin).getWorldsHolder().getWorldPermissions(player).getGroupSuffix(gmGroup);
			db.i("GroupManager returned group suffix: " + suffix);
			break;
		}
		
		if (suffix.equals("") || suffix == null) {
			db.i("Permissions plugins did not return any suffix, checking permissions...");
			suffix = wildcard.getGroupSuffix(player);
		}
		
		return (suffix.equals("") || suffix == null) ? "" : suffix;
	}
	
	/**
	 * Gets the Player prefix
	 * 
	 * @param player The Player to check
	 * 
	 * @return The Player prefix
	 */
	public String getPlayerPrefix(Player player) {
		String prefix = "";
		
		switch (using()) {
		
		case PERMISSIONSEX:
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			prefix = (user != null) ? user.getPrefix() : "";
			db.i("PermissionsEx returned player prefix: " + prefix);
			break;
			
		case BPERMISSIONS:
			String[] bGroups = ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
			String bGroup = (bGroups != null && bGroups.length > 0) ? bGroups[0] : "";
			prefix = (!bGroup.equals("")) ? ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, bGroup, "prefix") : "";
			db.i("bPermissions returned player prefix: " + prefix);
			break;
			
		case GROUPMANAGER:
			prefix = ((GroupManager) permissionsPlugin).getWorldsHolder().getWorldPermissions(player).getUserPrefix(player.getName());
			db.i("GroupManager returned player prefix: " + prefix);
			break;
		}
		
		if (prefix.equals("") || prefix == null) {
			db.i("Permissions plugins did not return any prefix, checking permissions...");
			prefix = wildcard.getPlayerPrefix(player);
		}
		
		return (prefix.equals("") || prefix == null) ? getGroupPrefix(player) : prefix;
	}
	
	/**
	 * Gets the Player suffix
	 * 
	 * @param player The Player to check
	 * 
	 * @return The Player prefix
	 */
	public String getPlayerSuffix(Player player) {
		String suffix = "";
		
		switch (using()) {
		
		case PERMISSIONSEX:
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			suffix = (user != null) ? user.getSuffix() : "";
			db.i("PermissionsEx returned player suffix: " + suffix);
			break;
			
		case BPERMISSIONS:
			String[] bGroups = ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
			String bGroup = (bGroups != null && bGroups.length > 0) ? bGroups[0] : "";
			suffix = (!bGroup.equals("")) ? ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, bGroup, "suffix") : "";
			db.i("bPermissions returned player suffix: " + suffix);
			break;
			
		case GROUPMANAGER:
			suffix = ((GroupManager) permissionsPlugin).getWorldsHolder().getWorldPermissions(player).getUserSuffix(player.getName());
			db.i("GroupManager returned player suffix: " + suffix);
			break;
		}
		
		if (suffix.equals("") || suffix == null) {
			db.i("Permissions plugins did not return any suffix, checking permissions...");
			suffix = wildcard.getPlayerSuffix(player);
		}
		
		return (suffix.equals("") || suffix == null) ? getGroupSuffix(player) : suffix;
	}
	
	/**
	 * Gets the Wildcard avoider
	 * 
	 * @return The Wildcard avoider
	 */
	public WildcardNodes getWildcardAvoider() {
		return wildcard;
	}
	
	/**
	 * Check if a Player has a permission
	 * 
	 * @param player The Player to be checked
	 * 
	 * @param permission The permission to be checked
	 * 
	 * @return True if the Player has the permission
	 */
	public boolean has(Player player, String permission) {
		switch (using()) {
		
		case PERMISSIONSEX:
			return PermissionsEx.getPermissionManager().getUser(player).has(permission, player.getWorld().getName());
			
		case BPERMISSIONS:
			return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), permission);
			
		case GROUPMANAGER:
			return ((GroupManager) permissionsPlugin).getWorldsHolder().getWorldPermissions(player.getWorld().getName()).permission(player, permission);
		}
		
		return player.hasPermission(permission);
	}
	
	/**
	 * Listens to the PluginDisableEvent
	 * 
	 * @param event PlayerDisableEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event) {
		if (permissionsPlugin != null) {
			if (event.getPlugin().getName().equals(name)) {
				permissionsPlugin = null;
				if (!plugin.usingVault()) { plugin.log(Level.INFO, name + " unhooked"); }
			}
		}
	}
	
	/**
	 * Listens to the PluginEnableEvent
	 * 
	 * @param event PluginEnableEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
		if (permissionsPlugin == null) {
			Plugin perms = null;
			
			if (perms == null) {
				if (exists("ru.tehkode.permissions.bukkit.PermissionsEx"))
					perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
				
				else if (exists("de.bananaco.bpermissions.imp.Permissions"))
					perms = plugin.getServer().getPluginManager().getPlugin("bPermissions");
				
				else if (exists("com.platymuus.bukkit.permissions.PermissionsPlugin"))
					perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
				
				else if (exists("org.anjocaido.groupmanager.GroupManager"))
					perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");
				
				else if (exists("org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin"))
					perms = plugin.getServer().getPluginManager().getPlugin("zPermissions");
			}
			
			if (perms != null) {
				if (perms.isEnabled()) {
					permissionsPlugin = perms;
					name = permissionsPlugin.getName();
				}
				
			} else { if (!plugin.usingVault()) { plugin.log(Level.INFO, name + " detected and hooked"); } }
		}
	}
	
	public PermissionsPlugin using() {
		if (permissionsPlugin != null)
			return PermissionsPlugin.fromName(permissionsPlugin.getName());
		
		return PermissionsPlugin.SUPERPERMS;
	}
	
	public enum PermissionsPlugin {
		PERMISSIONSEX("PermissionsEx"),
		BPERMISSIONS("bPermissions"),
		SUPERPERMS("SuperPerms"),
		PERMISSIONSBUKKIT("PermissionsBukkit"),
		GROUPMANAGER("GroupManager"),
		ZPERMISSIONS("zPermissions");
		
		private String name;
		
		private static Map<String, PermissionsPlugin> NAME_MAP = new HashMap<String, PermissionsPlugin>();
		
		private PermissionsPlugin(String name) {
			this.name = name;
		}
		
		static {
			for (PermissionsPlugin permission : EnumSet.allOf(PermissionsPlugin.class)) {
				NAME_MAP.put(permission.name, permission);
			}
		}
		
		public static PermissionsPlugin fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		public String getName() {
			return name;
		}
	}
}