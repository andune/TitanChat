package com.titankingdoms.nodinchan.titanchat.channel;

import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.enums.Type;

public abstract interface ChannelInterface {
	
	public abstract boolean canAccess(Player player);
	
	public abstract boolean canBan(Player player);
	
	public abstract boolean canKick(Player player);
	
	public abstract boolean canMute(Player player);
	
	public abstract boolean canRank(Player player);
	
	public abstract List<String> getAdminList();
	
	public abstract List<String> getBlackList();
	
	public abstract List<String> getFollowerList();
	
	public abstract List<String> getInviteList();
	
	public abstract List<String> getMuteList();
	
	public abstract String getName();
	
	public abstract String getPassword();
	
	public abstract Type getType();
	
	public abstract List<String> getWhiteList();
	
	public abstract boolean isGlobal();
	
	public abstract boolean isSilenced();
	
	public abstract void join(Player player);
	
	public abstract List<String> getParticipants();
	
	public abstract void leave(Player player);
	
	public abstract void save();
	
	public abstract void sendMessage(String message);
	
	public abstract void setGlobal(boolean global);
	
	public abstract void setPassword(String password);
	
	public abstract void setSilenced(boolean silenced);
}