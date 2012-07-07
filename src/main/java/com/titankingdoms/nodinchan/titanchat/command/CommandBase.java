package com.titankingdoms.nodinchan.titanchat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nodinchan.ncbukkit.loader.Loadable;
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
public class CommandBase extends Loadable implements Listener {

	protected final TitanChat plugin;
	
	/**
	 * Initialises variables
	 */
	public CommandBase() {
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
		Executor executor = plugin.getManager().getCommandManager().getCommandExecutor(name);
		
		if (!executor.getUsage().equals(""))
			plugin.sendInfo(player, "Usage: /titanchat " + executor.getUsage());
	}
	
	/**
	 * Registers the addon
	 * 
	 * @param addon the addon to register
	 */
	public final void register(Addon addon) {
		plugin.getManager().getAddonManager().register(addon);
	}
	
	/**
	 * Registers the custom channel
	 * 
	 * @param channel the channel to register
	 */
	public final void register(CustomChannel channel) {
		plugin.getManager().getChannelManager().register(channel);
	}
	
	/**
	 * Registers the Listener
	 * 
	 * @param listener The Listener to register
	 */
	public final void register(Listener listener) {
		plugin.register(listener);
	}
}