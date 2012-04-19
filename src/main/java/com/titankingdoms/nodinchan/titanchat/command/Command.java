package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nodinchan.ncloader.Loadable;
import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.addon.Addon;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;

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

/**
 * Command - Command base
 * 
 * @author NodinChan
 *
 */
public class Command extends Loadable implements Listener {

	protected final TitanChat plugin;
	
	public Command() {
		super("");
		this.plugin = TitanChat.getInstance();
	}
	
	/**
	 * Sends a warning for invalid argument length
	 * 
	 * @param player the player to send to
	 * 
	 * @param name the command's name
	 */
	public final void invalidArgLength(Player player, String name) {
		plugin.sendWarning(player, "Invalid Argument Length");
		
		Method method = plugin.getCommandManager().getCommandExecutor(name).getMethod();
		
		if (method.getAnnotation(CommandInfo.class) != null)
			plugin.sendInfo(player, "Usage: /titanchat " + method.getAnnotation(CommandInfo.class).usage());
	}
	
	/**
	 * Registers the addon
	 * 
	 * @param addon the addon to register
	 */
	public final void register(Addon addon) {
		plugin.getAddonManager().register(addon);
	}
	
	/**
	 * Registers the custom channel
	 * 
	 * @param channel the channel to register
	 */
	public final void register(CustomChannel channel) {
		plugin.getChannelManager().register(channel);
	}
	
	/**
	 * Registers the Listener
	 * 
	 * @param listener The Listener to register
	 */
	public final void register(Listener listener) {
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
}