package com.titankingdoms.nodinchan.titanchat.commands;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

@SuppressWarnings("unused")
public class MiniCommandHandler {
	
	private TitanChat plugin;

	private Administrate admin;
	private ChannelSettings chSettings;
	private Invite invite;
	
	public MiniCommandHandler(TitanChat plugin) {
		this.plugin = plugin;
		this.admin = new Administrate(plugin);
		this.chSettings = new ChannelSettings(plugin);
		this.invite = new Invite(plugin);
	}
	
	public Administrate getAdmin() {
		return admin;
	}
	
	public ChannelSettings getChannelSettings() {
		return chSettings;
	}
	
	public Invite getInvite() {
		return invite;
	}
}
