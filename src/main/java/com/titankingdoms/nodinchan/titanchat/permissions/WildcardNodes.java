package com.titankingdoms.nodinchan.titanchat.permissions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class WildcardNodes {
	
	private Plugin plugin;
	
	public WildcardNodes(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public boolean has(Player player, String permission) {
		if (plugin instanceof PermissionsEx) {
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			for (String perm : user.getPermissions(player.getWorld().getName())) {
				if (perm.equals(permission))
					return true;
			}
			
		}
		
		return TitanChat.getInstance().has(player, permission);
	}
}