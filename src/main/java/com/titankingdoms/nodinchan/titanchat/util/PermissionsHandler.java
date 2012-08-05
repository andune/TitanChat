package com.titankingdoms.nodinchan.titanchat.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.util.PermissionsHandler.Affix.Group;
import com.titankingdoms.nodinchan.titanchat.util.PermissionsHandler.Affix.Type;

public final class PermissionsHandler {
	
	private final TitanChat plugin;
	
	private static final Debugger db = new Debugger(5);
	
	private final Map<Group, Map<Type, Map<String, Affix>>> affixes;
	
	private final Vault vault;
	
	public PermissionsHandler() {
		this.plugin = TitanChat.getInstance();
		this.affixes = new HashMap<Group, Map<Type, Map<String, Affix>>>();
		this.vault = new Vault();
		
		if (plugin.getServer().getPluginManager().getPlugin("Vault") != null)
			vault.setup();
	}
	
	public String getGroupPrefix(Player player) {
		String prefix = "";
		db.i("PermissionsHandler: Getting group prefix of " + player.getName());
		
		for (String permission : affixes.get(Group.GROUP).get(Type.PRE).keySet()) {
			if (!has(player, permission))
				continue;
			
			prefix = affixes.get(Group.GROUP).get(Type.PRE).get(permission).getAffix();
			break;
		}
		
		if (prefix.isEmpty())
			prefix = vault.getGroupPrefix(player);
		
		prefix = (prefix != null) ? prefix : "";
		db.i("PermissionsHandler: Returning " + prefix);
		return prefix;
	}
	
	public String getGroupSuffix(Player player) {
		String suffix = "";
		db.i("PermissionsHandler: Getting group suffix of " + player.getName());
		
		for (String permission : affixes.get(Group.GROUP).get(Type.SUF).keySet()) {
			if (!has(player, permission))
				continue;
			
			suffix = affixes.get(Group.GROUP).get(Type.SUF).get(permission).getAffix();
			break;
		}
		
		if (suffix.isEmpty())
			suffix = vault.getGroupSuffix(player);
		
		suffix = (suffix != null) ? suffix : "";
		db.i("PermissionsHandler: Returning " + suffix);
		return suffix;
	}
	
	public String getPlayerPrefix(Player player) {
		String prefix = "";
		db.i("PermissionsHandler: Getting player prefix of " + player.getName());
		
		for (String permission : affixes.get(Group.PLAYER).get(Type.PRE).keySet()) {
			if (!has(player, permission))
				continue;
			
			prefix = affixes.get(Group.PLAYER).get(Type.PRE).get(permission).getAffix();
			break;
		}
		
		if (prefix.isEmpty())
			prefix = vault.getPlayerPrefix(player);
		
		if (prefix.isEmpty())
			prefix = getGroupPrefix(player);
		
		db.i("PermissionsHandler: Returning " + prefix);
		return prefix;
	}
	
	public String getPlayerSuffix(Player player) {
		String suffix = "";
		db.i("PermissionsHandler: Getting player suffix of " + player.getName());
		
		for (String permission : affixes.get(Group.PLAYER).get(Type.SUF).keySet()) {
			if (!has(player, permission))
				continue;
			
			suffix = affixes.get(Group.PLAYER).get(Type.SUF).get(permission).getAffix();
			break;
		}
		
		if (suffix.isEmpty())
			suffix = vault.getPlayerSuffix(player);
		
		if (suffix.isEmpty())
			suffix = getGroupPrefix(player);
		
		db.i("PermissionsHandler: Returning " + suffix);
		return suffix;
	}
	
	public boolean has(Player player, String permission) {
		if (vault.isPermissionSetup())
			return vault.has(player, permission);
		
		return player.hasPermission(permission);
	}
	
	public void load() {
		this.affixes.put(Group.GROUP, new HashMap<Type, Map<String, Affix>>());
		this.affixes.put(Group.PLAYER, new HashMap<Type, Map<String, Affix>>());
		this.affixes.get(Group.GROUP).put(Type.PRE, new HashMap<String, Affix>());
		this.affixes.get(Group.GROUP).put(Type.SUF, new HashMap<String, Affix>());
		this.affixes.get(Group.PLAYER).put(Type.PRE, new HashMap<String, Affix>());
		this.affixes.get(Group.PLAYER).put(Type.SUF, new HashMap<String, Affix>());
		
		if (plugin.getConfig().getConfigurationSection("permissions") == null)
			return;
		
		PermissionDefault def = PermissionDefault.FALSE;
		
		if (plugin.getConfig().getConfigurationSection("permissions.group") != null) {
			String perm = "TitanChat.g.";
			
			for (String group : plugin.getConfig().getConfigurationSection("permissions.group").getKeys(false)) {
				if (plugin.getConfig().get("permissions.group." + group + ".prefix") != null) {
					String prefix = plugin.getConfig().getString("permissions.group." + group + ".prefix");
					
					Permission permission = new Permission(perm + ".prefix." + group, "Group Prefix " + prefix, def);
					plugin.getServer().getPluginManager().addPermission(permission);
					
					Affix affix = new Affix(prefix, permission, Type.PRE, Group.GROUP);
					affixes.get(Group.GROUP).get(Type.PRE).put(permission.getName(), affix);
					
					db.i("PermissionsHandler: Registered " + permission.getName());
				}
				
				if (plugin.getConfig().get("permissions.group." + group + ".suffix") != null) {
					String suffix = plugin.getConfig().getString("permissions.group." + group + ".suffix");
					
					Permission permission = new Permission(perm + ".suffix." + group, "Group Suffix " + suffix, def);
					plugin.getServer().getPluginManager().addPermission(permission);
					
					Affix affix = new Affix(suffix, permission, Type.SUF, Group.GROUP);
					affixes.get(Group.GROUP).get(Type.SUF).put(permission.getName(), affix);
					
					db.i("PermissionsHandler: Registered " + permission.getName());
				}
			}
		}
		
		if (plugin.getConfig().getConfigurationSection("permissions.player") != null) {
			String perm = "TitanChat.p.";
			
			for (String player : plugin.getConfig().getConfigurationSection("permissions.player").getKeys(false)) {
				if (plugin.getConfig().get("permissions.player." + player + ".prefix") != null) {
					String prefix = plugin.getConfig().getString("permissions.player." + player + ".prefix");
					
					Permission permission = new Permission(perm + ".prefix." + player, "Player Prefix " + prefix, def);
					plugin.getServer().getPluginManager().addPermission(permission);
					
					Affix affix = new Affix(prefix, permission, Type.PRE, Group.PLAYER);
					affixes.get(Group.PLAYER).get(Type.PRE).put(permission.getName(), affix);
					
					db.i("PermissionsHandler: Registered " + permission.getName());
				}
				
				if (plugin.getConfig().get("permissions.player." + player + ".suffix") != null) {
					String suffix = plugin.getConfig().getString("permissions.player." + player + ".suffix");
					
					Permission permission = new Permission(perm + ".suffix." + player, "Player Suffix " + suffix, def);
					plugin.getServer().getPluginManager().addPermission(permission);
					
					Affix affix = new Affix(suffix, permission, Type.SUF, Group.PLAYER);
					affixes.get(Group.PLAYER).get(Type.SUF).put(permission.getName(), affix);
					
					db.i("PermissionsHandler: Registered " + permission.getName());
				}
			}
		}
	}
	
	public static final class Affix {
		
		private final String affix;
		
		private final Permission permission;
		
		private final Type type;
		
		private final Group group;
		
		public Affix(String affix, Permission permission, Type type, Group group) {
			this.affix = affix;
			this.permission = permission;
			this.type = type;
			this.group = group;
		}
		
		public String getAffix() {
			return affix;
		}
		
		public Group getGroup() {
			return group;
		}
		
		public String getPermission() {
			return permission.getName();
		}
		
		public Type getType() {
			return type;
		}
		
		public enum Type { PRE, SUF }
		public enum Group { GROUP, PLAYER }
	}
	
	public final class Vault {
		
		private net.milkbowl.vault.permission.Permission perm;
		private net.milkbowl.vault.chat.Chat chat;
		
		public String getGroupPrefix(Player player) {
			if (chat != null && perm != null)
				return chat.getGroupPrefix(player.getWorld(), perm.getPrimaryGroup(player.getWorld(), player.getName()));
			else
				return "";
		}
		
		public String getGroupSuffix(Player player) {
			if (chat != null && perm != null)
				return chat.getGroupSuffix(player.getWorld(), perm.getPrimaryGroup(player.getWorld(), player.getName()));
			else
				return "";
		}
		
		public String getPlayerPrefix(Player player) {
			if (chat != null && perm != null)
				return chat.getPlayerPrefix(player.getWorld(), player.getName());
			else
				return getGroupPrefix(player);
		}
		
		public String getPlayerSuffix(Player player) {
			if (chat != null && perm != null)
				return chat.getPlayerSuffix(player.getWorld(), player.getName());
			else
				return getGroupSuffix(player);
		}
		
		public boolean has(Player player, String permission) {
			if (perm != null)
				return perm.has(player.getWorld(), player.getName(), permission);
			
			return player.hasPermission(permission);
		}
		
		public boolean isChatSetup() {
			return chat != null;
		}
		
		public boolean isPermissionSetup() {
			return perm != null;
		}
		
		public void setup() {
			db.i("PermissionsHandler: Setting up Vault Services");
			
			RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
			
			if (chatProvider != null)
				chat = chatProvider.getProvider();
			
			db.i("PermissionsHandler: Vault Chat Service is set up: " + (chat != null));
			
			RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			
			if (permissionProvider != null)
				perm = permissionProvider.getProvider();
			
			db.i("PermissionsHandler: Vault Permission Service is set up: " + (perm != null));
		}
	}
}