package com.titankingdoms.nodinchan.titanchat.permissions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class MiniPerms {
	
	private TitanChat plugin;
	
	private List<Group> groups;
	private List<User> users;
	
	public MiniPerms(TitanChat plugin) {
		this.plugin = plugin;
		this.groups = new ArrayList<Group>();
		this.users = new ArrayList<User>();
	}
	
	public Group getGroup(String name) {
		return getUser(name).getGroup();
	}
	
	public String getGroupPrefix(String name) {
		if (getGroup(name) != null)
			return getGroup(name).getPrefix();
		
		return "";
	}
	
	public String getGroupSuffix(String name) {
		if (getGroup(name) != null)
			return getGroup(name).getSuffix();
		
		return "";
	}
	
	public List<String> getPermissions(Player player) {
		List<String> permissions = new ArrayList<String>();
		
		found:
			for (Group group : groups) {
				for (User user : group.getUsers()) {
					if (user.getName().equals(player.getName())) {
						permissions.addAll(group.getPermissions());
						permissions.addAll(user.getPermissions());
						break found;
					}
				}
			}
		
		return permissions;
	}
	
	public User getUser(String name) {
		for (User user : users) {
			if (user.getName().equals(name))
				return user;
		}
		
		return null;
	}
	
	public String getUserPrefix(String name) {
		if (getUser(name) != null)
			return getUser(name).getPrefix();
		
		return getGroupPrefix(name);
	}
	
	public String getUserSuffix(String name) {
		if (getUser(name) != null)
			return getUser(name).getSuffix();
		
		return getGroupSuffix(name);
	}
	
	public boolean has(Player player, String permission) {
		for (String permissionNode : getPermissions(player)) {
			if (permissionNode.substring(1).equals(permission.substring(10)))
				return false;
		}
		
		return getPermissions(player).contains(permission.substring(10));
	}
	
	public void load() {
		if (plugin.getPermissions().getConfigurationSection("groups").getKeys(false) != null) {
			for (String groupName : plugin.getPermissions().getConfigurationSection("groups").getKeys(false)) {
				Group group = new Group(groupName);
				
				List<String> groupPermissions = plugin.getPermissions().getStringList("groups." + groupName + ".permissions");
				
				if (groupPermissions != null) { group.getPermissions().addAll(groupPermissions); }
				
				String groupPrefix = plugin.getPermissions().getString("groups." + groupName + ".prefix");
				String groupSuffix = plugin.getPermissions().getString("groups." + groupName + ".suffix");
				
				if (groupPrefix != null) { group.setPrefix(groupPrefix); }
				if (groupSuffix != null) { group.setSuffix(groupSuffix); }
				
				if (plugin.getPermissions().getStringList("groups." + groupName + ".users") != null) {
					for (String userName : plugin.getPermissions().getStringList("groups." + groupName + ".users")) {
						User user = new User(userName, group);
						
						List<String> userPermissions = plugin.getPermissions().getStringList("users." + userName + ".permissions");
						
						if (userPermissions != null) { user.getPermissions().addAll(userPermissions); }
						
						String userPrefix = plugin.getPermissions().getString("users." + userName + ".prefix");
						String userSuffix = plugin.getPermissions().getString("users." + userName + ".suffix");
						
						if (userPrefix != null) { user.setPrefix(userPrefix); }
						if (userSuffix != null) { user.setSuffix(userSuffix); }
						
						group.getUsers().add(user);
						users.add(user);
					}
				}
				
				groups.add(group);
			}
		}
		
		if (plugin.getPermissions().getConfigurationSection("users").getKeys(false) != null) {
			for (String userName : plugin.getPermissions().getConfigurationSection("users").getKeys(false)) {
				if (userExists(userName))
					continue;
				
				User user = new User(userName, null);
				
				List<String> userPermissions = plugin.getPermissions().getStringList("users." + userName + ".permissions");
				
				if (userPermissions != null) { user.getPermissions().addAll(userPermissions); }
				
				String userPrefix = plugin.getPermissions().getString("users." + userName + ".prefix");
				String userSuffix = plugin.getPermissions().getString("users." + userName + ".suffix");
				
				if (userPrefix != null) { user.setPrefix(userPrefix); }
				if (userSuffix != null) { user.setSuffix(userSuffix); }
				
				users.add(user);
			}
		}
	}
	
	public boolean userExists(String name) {
		for (User user : users) {
			if (user.getName().equals(name))
				return true;
		}
		
		return false;
	}
}