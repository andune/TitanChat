package com.titankingdoms.nodinchan.titanchat.permissions;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.anjocaido.groupmanager.utils.PermissionCheckResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * WildcardNodes - Used for avoiding Wildcard nodes since they are sometimes a pain
 * 
 * @author NodinChan
 *
 */
public class WildcardNodes {
	
	private PermissionsHook hook;
	
	private static final Debugger db = new Debugger(5);
	
	public WildcardNodes(PermissionsHook hook) {
		this.hook = hook;
	}
	
	/**
	 * Check if the Player has a permission typed exactly instead of a '*'
	 * 
	 * @param player The Player to check
	 * 
	 * @param permission The permission
	 * 
	 * @return True if the Player has the permission
	 */
	public boolean has(Player player, String permission) {
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			if (permInfo.getPermission().equals(permission))
				return true;
		}
		
		switch (hook.using()) {
		
		case PERMISSIONSEX:
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			
			for (String perm : user.getPermissions(player.getWorld().getName())) {
				if (perm.equals(permission))
					return true;
			}
			break;
			
		case BPERMISSIONS:
			Permission[] perms = ApiLayer.getPermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
			
			for (Permission perm : perms) {
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
			
		case ZPERMISSIONS:
			
			break;
		}
		
		return TitanChat.getInstance().has(player, permission);
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
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			db.i("Checking if " + permInfo.getPermission() + " is a prefix permission");
			
			if (!(permInfo.getPermission().startsWith("TitanChat.g.prefix.")) || !(permInfo.getValue()))
				continue;
			
			prefix = permInfo.getPermission().substring(19);
			break;
		}
		
		if (prefix.equals("")) {
			switch (hook.using()) {
			
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
		
		return prefix;
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
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			db.i("Checking if " + permInfo.getPermission() + " is a suffix permission");
			
			if (!(permInfo.getPermission().startsWith("TitanChat.g.suffix.")) || !(permInfo.getValue()))
				continue;
			
			suffix = permInfo.getPermission().substring(19);
			break;
		}
		
		if (suffix.equals("")) {
			switch (hook.using()) {
			
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
		
		return suffix;
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
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			db.i("Checking if " + permInfo.getPermission() + " is a prefix permission");
			
			if (!(permInfo.getPermission().startsWith("TitanChat.p.prefix.")) || !(permInfo.getValue()))
				continue;
			
			prefix = permInfo.getPermission().substring(19);
			break;
		}
		
		if (prefix.equals("")) {
			switch (hook.using()) {
			
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
		
		return prefix;
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
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			db.i("Checking if " + permInfo.getPermission() + " is a suffix permission");
			
			if (!(permInfo.getPermission().startsWith("TitanChat.p.suffix.")) || !(permInfo.getValue()))
				continue;
			
			suffix = permInfo.getPermission().substring(19);
			break;
		}
		
		if (suffix.equals("")) {
			switch (hook.using()) {
			
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
		
		return suffix;
	}
	
	public void removePermission(Player player, String permission) {
		switch (hook.using()) {
		
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
}