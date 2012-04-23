package com.titankingdoms.nodinchan.titanchat.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

/**
 * ConfigPermsManagement - A temporary fix until Bukkit pulls Pull Request #590
 * 
 * @author NodinChan
 *
 */
public class ConfigPermsManagement {
	
	private final Map<String, Map<String, String>> prefixes;
	private final Map<String, Map<String, String>> suffixes;
	
	public ConfigPermsManagement(TitanChat plugin) {
		this.prefixes = new HashMap<String, Map<String, String>>();
		this.suffixes = new HashMap<String, Map<String, String>>();

		Map<String, String> pPrefixes = new HashMap<String, String>();
		Map<String, String> pSuffixes = new HashMap<String, String>();
		Map<String, String> gPrefixes = new HashMap<String, String>();
		Map<String, String> gSuffixes = new HashMap<String, String>();
		
		for (String permission : plugin.getConfig().getConfigurationSection("permissions").getKeys(false)) {
			if (permission.startsWith("TitanChat.p")) {
				if (permission.startsWith("TitanChat.p.prefix"))
					pPrefixes.put(permission, plugin.getConfig().getString("permissions." + permission));
				else if (permission.startsWith("TitanChat.p.suffix"))
					pSuffixes.put(permission, plugin.getConfig().getString("permissions." + permission));
				
			} else if (permission.startsWith("TitanChat.g")) {
				if (permission.startsWith("TitanChat.g.prefix"))
					gPrefixes.put(permission, plugin.getConfig().getString("permissions." + permission));
				else if (permission.startsWith("TitanChat.g.suffix"))
					gSuffixes.put(permission, plugin.getConfig().getString("permissions." + permission));
			}
		}
		
		prefixes.put("player", pPrefixes);
		prefixes.put("group", gPrefixes);
		suffixes.put("player", pSuffixes);
		suffixes.put("group", gSuffixes);
	}
	
	/**
	 * Gets the Group Prefix of the Player using the Config
	 * 
	 * @param player The Player to check for
	 * 
	 * @return The prefix
	 */
	public String getGroupPrefix(Player player) {
		String prefix = "";
		
		found:
			for (String permission : prefixes.get("group").keySet()) {
				for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
					if (!permInfo.getPermission().equalsIgnoreCase(permission) && !permInfo.getValue())
						continue;
					
					prefix = prefixes.get("group").get(permission);
					break found;
				}
			}
		
		return prefix;
	}
	
	/**
	 * Gets the Group Suffix of the Player using the Config
	 * 
	 * @param player The Player to check for
	 * 
	 * @return The suffix
	 */
	public String getGroupSuffix(Player player) {
		String suffix = "";
		
		found:
			for (String permission : suffixes.get("group").keySet()) {
				for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
					if (!permInfo.getPermission().equalsIgnoreCase(permission) && !permInfo.getValue())
						continue;
					
					suffix = suffixes.get("group").get(permission);
					break found;
				}
			}
		
		return suffix;
	}
	
	/**
	 * Gets the Player Prefix of the Player using the Config
	 * 
	 * @param player The Player to check for
	 * 
	 * @return The prefix
	 */
	public String getPlayerPrefix(Player player) {
		String prefix = "";
		
		found:
			for (String permission : prefixes.get("player").keySet()) {
				for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
					if (!permInfo.getPermission().equalsIgnoreCase(permission) && !permInfo.getValue())
						continue;
					
					prefix = prefixes.get("player").get(permission);
					break found;
				}
			}
		
		return prefix;
	}
	
	/**
	 * Gets the Player Suffix of the Player using the Config
	 * 
	 * @param player The Player to check for
	 * 
	 * @return The suffix
	 */
	public String getPlayerSuffix(Player player) {
		String suffix = "";
		
		found:
			for (String permission : suffixes.get("player").keySet()) {
				for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
					if (!permInfo.getPermission().equalsIgnoreCase(permission) && !permInfo.getValue())
						continue;
					
					suffix = suffixes.get("player").get(permission);
					break found;
				}
			}
		
		return suffix;
	}
}