package com.titankingdoms.nodinchan.titanchat.channel.standard;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.util.handler.CommandHandler;
import com.titankingdoms.nodinchan.titanchat.channel.util.handler.Handler.HandlerInfo;
import com.titankingdoms.nodinchan.titanchat.channel.util.Info;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public final class ServerChannel extends Channel {
	
	private static ServerChannel instance;
	
	private final Info info;
	
	public ServerChannel() {
		super("Server", Option.DEFAULT);
		ServerChannel.instance = this;
		load(null, null);
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
		registerCommandHandlers(
				new ServerCommandHandler("ban"),
				new ServerCommandHandler("dewhitelist"),
				new ServerCommandHandler("follow"),
				new ServerCommandHandler("force"),
				new ServerCommandHandler("invite"),
				new ServerCommandHandler("join"),
				new ServerCommandHandler("kick"),
				new ServerCommandHandler("leave"),
				new ServerCommandHandler("unban"),
				new ServerCommandHandler("unfollow"),
				new ServerCommandHandler("whitelist")
		);
		
		return this;
	}
	
	public static final class ServerCommandHandler extends CommandHandler {
		
		public ServerCommandHandler(String command) {
			super(ServerChannel.instance, command, new HandlerInfo("Command Disabled", command.toLowerCase()));
		}
		
		@Override
		public void onCommand(CommandSender sender, String[] args) {
			plugin.send(MessageLevel.WARNING, sender, "Channels Disabled");
		}
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
			return plugin.getConfig().getString("channels.chat-display-colour", "");
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
			return plugin.getConfig().getString("channels.name-display-colour", "");
		}
		
		@Override
		public String getTag() {
			return plugin.getConfig().getString("channels.tag", "");
		}
		
		@Override
		public int radius()  {
			return 0;
		}
		
		@Override
		public Range range() {
			return Range.GLOBAL;
		}
		
		@Override
		public void setChatColour(String colour) {}
		
		@Override
		public void setColouring(boolean colouring) {}
		
		@Override
		public void setFormat(String format) {}
		
		@Override
		public void setNameColour(String colour) {}
		
		@Override
		public void setRadius(int radius) {}
		
		@Override
		public void setRange(Range range) {}
		
		public void setTag(String tag) {}
	}
}