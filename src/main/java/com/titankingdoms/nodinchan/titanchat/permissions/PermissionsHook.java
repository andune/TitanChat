package com.titankingdoms.nodinchan.titanchat.permissions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.anjocaido.groupmanager.utils.PermissionCheckResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

import ru.tehkode.permissions.PermissionGroup;
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
	
	private static final Debugger db = new Debugger(5);
	
	private Plugin permissionsPlugin;
	
	private String name = "SuperPerms";
	
	private boolean checked = false;
	
	private Permission perm;
	private Chat chat;
	
	public PermissionsHook(TitanChat plugin) {
		this.plugin = plugin;
		
		if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
			setupChatService();
			setupPermissionService();
		}
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
		
		db.i("Getting group prefix of player " + player.getName());
		
		if (perm != null && chat != null) {
			prefix = chat.getGroupPrefix(player.getWorld(), perm.getPrimaryGroup(player));
			db.i("Returning: " + prefix);
			return (prefix != null) ? prefix : "";
		}
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			db.i("Checking if " + permInfo.getPermission() + " is a prefix permission");
			
			if (!(permInfo.getPermission().startsWith("TitanChat.g.prefix.")) || !(permInfo.getValue()))
				continue;
			
			prefix = permInfo.getPermission().substring(19);
			break;
		}
		
		if (prefix.equals("")) {
			switch (using()) {
			
			case PERMISSIONSEX:
				db.i("Prefix not found with permission attachments, checking PermissionsEx");
				
				PermissionGroup[] groups = PermissionsEx.getPermissionManager().getUser(player).getGroups(player.getWorld().getName());
				
				if (groups != null && groups.length > 0) {
					for (String perm : groups[0].getPermissions(player.getWorld().getName())) {
						db.i("Checking if " + perm + " is a prefix permission");
						
						if (perm.startsWith("TitanChat.g.prefix.")) {
							prefix = perm.substring(19);
							db.i("PermissionsEx permissions returned prefix: " + prefix);
						}
					}
				}
				break;
			}
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
		
		db.i("Getting group suffix of player " + player.getName());
		
		if (perm != null && chat != null) {
			suffix = chat.getGroupSuffix(player.getWorld(), perm.getPrimaryGroup(player));
			db.i("Returning: " + suffix);
			return (suffix != null) ? suffix : "";
		}
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			db.i("Checking if " + permInfo.getPermission() + " is a suffix permission");
			
			if (!(permInfo.getPermission().startsWith("TitanChat.g.suffix.")) || !(permInfo.getValue()))
				continue;
			
			suffix = permInfo.getPermission().substring(19);
			break;
		}
		
		if (suffix.equals("")) {
			switch (using()) {
			
			case PERMISSIONSEX:
				db.i("Suffix not found with permission attachments, checking PermissionsEx");
				
				PermissionGroup[] groups = PermissionsEx.getPermissionManager().getUser(player).getGroups(player.getWorld().getName());
				
				if (groups != null && groups.length > 0) {
					for (String perm : groups[0].getPermissions(player.getWorld().getName())) {
						if (perm.startsWith("TitanChat.g.suffix.")) {
							suffix = perm.substring(19);
							db.i("PermissionsEx permissions returned suffix: " + suffix);
						}
					}
				}
				break;
			}
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
		
		db.i("Getting prefix of player: " + player.getName());
		
		if (chat != null) {
			prefix = chat.getPlayerPrefix(player.getWorld(), player.getName());
			db.i("Returning: " + prefix);
			return (prefix != null) ? prefix : getGroupPrefix(player);
		}
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			db.i("Checking if " + permInfo.getPermission() + " is a prefix permission");
			
			if (!(permInfo.getPermission().startsWith("TitanChat.p.prefix.")) || !(permInfo.getValue()))
				continue;
			
			prefix = permInfo.getPermission().substring(19);
			break;
		}
		
		if (prefix.equals("")) {
			switch (using()) {
			
			case PERMISSIONSEX:
				db.i("Prefix not found with permission attachments, checking PermissionsEx");
				
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
				for (String perm : user.getPermissions(player.getWorld().getName())) {
					if (perm.startsWith("TitanChat.p.prefix.")) {
						prefix = perm.substring(19);
						db.i("PermissionsEx permissions returned prefix: " + prefix);
					}
				}
				break;
			}
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
		
		db.i("Getting suffix of player: " + player.getName());
		
		if (chat != null) {
			suffix = chat.getPlayerSuffix(player.getWorld(), player.getName());
			db.i("Returning: " + suffix);
			return (suffix != null) ? suffix : getGroupSuffix(player);
		}
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			db.i("Checking if " + permInfo.getPermission() + " is a suffix permission");
			
			if (!(permInfo.getPermission().startsWith("TitanChat.p.suffix.")) || !(permInfo.getValue()))
				continue;
			
			suffix = permInfo.getPermission().substring(19);
			break;
		}
		
		if (suffix.equals("")) {
			switch (using()) {
			
			case PERMISSIONSEX:
				db.i("Suffix not found with permission attachments, checking PermissionsEx");
				
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
				for (String perm : user.getPermissions(player.getWorld().getName())) {
					if (perm.startsWith("TitanChat.p.suffix.")) {
						suffix = perm.substring(19);
						db.i("PermissionsEx permissions returned suffix: " + suffix);
					}
				}
				break;
			}
		}
		
		return (suffix.equals("") || suffix == null) ? getGroupSuffix(player) : suffix;
	}
	
	public boolean has(Player player, String permission) {
		return has(player, permission, false);
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
	public boolean has(Player player, String permission, boolean avoidWildcard) {
		if (avoidWildcard) {
			for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
				if (permInfo.getPermission().equals(permission))
					return true;
			}
			
			switch (using()) {
			
			case PERMISSIONSEX:
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
				
				for (String perm : user.getPermissions(player.getWorld().getName())) {
					if (perm.equals(permission))
						return true;
				}
				break;
				
			case BPERMISSIONS:
				de.bananaco.bpermissions.api.util.Permission[] perms = ApiLayer.getPermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
				
				for (de.bananaco.bpermissions.api.util.Permission perm : perms) {
					if (perm.name().equals(permission))
						return true;
				}
				break;
				
			case PERMISSIONSBUKKIT:
				PermissionsPlugin permBukkit = (PermissionsPlugin) TitanChat.getInstance().getServer().getPluginManager().getPlugin("PermissionsBukkit");
				
				for (String perm : permBukkit.getPlayerInfo(player.getName()).getPermissions().keySet()) {
					if (perm.equals(permission) && permBukkit.getPlayerInfo(player.getName()).getPermissions().get(perm))
						return true;
				}
				break;
				
			case GROUPMANAGER:
				OverloadedWorldHolder holder = ((GroupManager) TitanChat.getInstance().getServer().getPluginManager().getPlugin("GroupManager")).getWorldsHolder().getWorldDataByPlayerName(player.getName());
				AnjoPermissionsHandler handler = ((GroupManager) TitanChat.getInstance().getServer().getPluginManager().getPlugin("GroupManager")).getWorldsHolder().getWorldPermissionsByPlayerName(player.getName());
				
				if (holder != null && handler != null) {
					User gmUser = holder.getUser(player.getName());
					
					if (gmUser != null) {
						PermissionCheckResult result = handler.checkFullGMPermission(gmUser, permission, false);
						return result.resultType.equals(PermissionCheckResult.Type.EXCEPTION) || result.resultType.equals(PermissionCheckResult.Type.FOUND);
					}
				}
				break;
			}
		}
		
		if (usingVault())
			return perm.has(player, permission);
		
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
				if (!usingVault()) { plugin.log(Level.INFO, name + " unhooked"); }
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
				
			} else { if (!usingVault() && !checked) { plugin.log(Level.INFO, name + " detected and hooked"); checked = true; } }
		}
	}
	
	public void removePermission(Player player, String permission) {
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			if (!permInfo.getPermission().equals(permission))
				continue;
			
			permInfo.getAttachment().unsetPermission(permission);
			return;
		}
		
		switch (using()) {
		
		case PERMISSIONSEX:
			PermissionUser pexUser = PermissionsEx.getPermissionManager().getUser(player);
			pexUser.removePermission(permission);
			break;
			
		case BPERMISSIONS:
			ApiLayer.removePermission(player.getWorld().getName(), CalculableType.USER, player.getName(), permission);
			break;
			
		case PERMISSIONSBUKKIT:
			TitanChat.getInstance().getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player.getName() + " " + player.getWorld().getName() + ":" + permission);
			break;
			
		case GROUPMANAGER:
			OverloadedWorldHolder holder = ((GroupManager) TitanChat.getInstance().getServer().getPluginManager().getPlugin("GroupManager")).getWorldsHolder().getWorldDataByPlayerName(player.getName());
			if (holder != null) {
				User gmUser = holder.getUser(player.getName());
				if (gmUser != null) { gmUser.removePermission(permission); }
			}
			break;
			
		case ZPERMISSIONS:
			TitanChat.getInstance().getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unset " + player.getName() + " " + permission);
			break;
		}
	}
	
	/**
	 * Sets up the Chat Service of Vault
	 * 
	 * @return True if a Chat Service is present
	 */
	public boolean setupChatService() {
		RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
		
		if (chatProvider != null)
			chat = chatProvider.getProvider();

		db.i("Vault Chat Service is set up: " + (chat != null));
		return chat != null;
	}
	
	/**
	 * Sets up the Permission Service of Vault
	 * 
	 * @return True if a Permission Service is present
	 */
	public boolean setupPermissionService() {
		RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
		
		if (permissionProvider != null)
			perm = permissionProvider.getProvider();
		
		db.i("Vault Permission Service is set up: " + (perm != null));
		return perm != null;
	}
	
	public Permissions using() {
		if (permissionsPlugin != null)
			return Permissions.fromName(permissionsPlugin.getName());
		
		return Permissions.SUPERPERMS;
	}
	
	public boolean usingVault() {
		return perm != null;
	}
	
	public enum Permissions {
		PERMISSIONSEX("PermissionsEx"),
		BPERMISSIONS("bPermissions"),
		SUPERPERMS("SuperPerms"),
		PERMISSIONSBUKKIT("PermissionsBukkit"),
		GROUPMANAGER("GroupManager"),
		ZPERMISSIONS("zPermissions");
		
		private String name;
		
		private static Map<String, Permissions> NAME_MAP = new HashMap<String, Permissions>();
		
		private Permissions(String name) {
			this.name = name;
		}
		
		static {
			for (Permissions permission : EnumSet.allOf(Permissions.class)) {
				NAME_MAP.put(permission.name, permission);
			}
		}
		
		public static Permissions fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		public String getName() {
			return name;
		}
	}
}