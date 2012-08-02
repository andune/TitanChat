package com.titankingdoms.nodinchan.titanchat.channel.standard;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.util.CommandHandler;
import com.titankingdoms.nodinchan.titanchat.channel.util.Handler.HandlerInfo;
import com.titankingdoms.nodinchan.titanchat.channel.util.Info;

public final class ServerChannel extends Channel {
	
	private final Info info;
	
	public ServerChannel() {
		super("Server", Option.DEFAULT);
		load(getName(), getOption());
		this.info = new ServerInfo();
	}
	
	@Override
	public boolean access(Player player) {
		return true;
	}
	
	@Override
	public Channel create(CommandSender sender, String name, Option option) {
		return this;
	}
	
	@Override
	public Info getInfo() {
		return info;
	}
	
	@Override
	public String getType() {
		return "Server";
	}
	
	@Override
	public Channel load(String name, Option option) {
		registerCommandHandlers(new CommandHandler(this, "Ban", new HandlerInfo(null, null, 0, 0)) {

			@Override
			public void onCommand(CommandSender sender, String[] args) { plugin.send(MessageLevel.WARNING, sender, "Administration command disabled"); }
			
		}, new CommandHandler(this, "Join", new HandlerInfo(null, null, 0, 0)) {

			@Override
			public void onCommand(CommandSender sender, String[] args) {}
			
		}, new CommandHandler(this, "Kick", new HandlerInfo(null, null, 0, 0)) {

			@Override
			public void onCommand(CommandSender sender, String[] args) { plugin.send(MessageLevel.WARNING, sender, "Administration command disabled"); }
			
		}, new CommandHandler(this, "Leave", new HandlerInfo(null, null, 0, 0)) {

			@Override
			public void onCommand(CommandSender sender, String[] args) {}
		});
		
		return this;
	}
	
	public static final class ServerInfo extends Info {
		
		public ServerInfo() {
			super(null);
		}
		
		@Override
		public boolean colouring() {
			return true;
		}
		
		@Override
		public boolean enableJoinMessage() {
			return false;
		}
		
		@Override
		public boolean enableLeaveMessage() {
			return false;
		}
		
		@Override
		public String getChatColour() {
			return plugin.getConfig().getString("channels.chat-display-colour");
		}
		
		@Override
		public String getFormat() {
			if (plugin.getConfig().getBoolean("formatting.use-custom-format"))
				return plugin.getConfig().getString("formatting.format");
			else
				return "<%prefix%player%suffix&f> %message";
		}
		
		@Override
		public String getNameColour() {
			return plugin.getConfig().getString("channels.name-display-colour");
		}
		
		@Override
		public String getTag() {
			return plugin.getConfig().getString("channels.tag");
		}
		
		@Override
		public void setChatColour(String colour) {}
		
		@Override
		public void setColouring(boolean colouring) {}
		
		@Override
		public void setFormat(String format) {}
		
		@Override
		public void setNameColour(String colour) {}
		
		public void setTag(String tag) {}
	}
}