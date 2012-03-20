package com.titankingdoms.nodinchan.titanchat.permissions;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

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
		switch (hook.using()) {
		
		case PERMISSIONSEX:
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			for (String perm : user.getPermissions(player.getWorld().getName())) {
				if (perm.equals(permission))
					return true;
			}
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
}