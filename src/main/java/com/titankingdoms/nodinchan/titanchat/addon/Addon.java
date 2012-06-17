package com.titankingdoms.nodinchan.titanchat.addon;

import java.util.logging.Logger;

import org.bukkit.event.Listener;

import com.nodinchan.ncloader.Loadable;
import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.command.Command;

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
 * Addon - Addon base
 * 
 * @author NodinChan
 *
 */
public class Addon extends Loadable implements Listener {
	
	protected final TitanChat plugin;
	
	private Logger log = Logger.getLogger("TitanLog");
	
	/**
	 * Addons for supporting other plugins
	 * 
	 * @param name The name of the Addon
	 */
	public Addon(String name) {
		super(name);
		this.plugin = TitanChat.getInstance();
	}
	
	/**
	 * Check if an Addon equals another
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof Addon)
			return ((Addon) object).getName().equals(getName());
		
		return false;
	}
	
	/**
	 * Gets the Logger
	 * 
	 * @return The Logger
	 */
	public Logger getLogger() {
		return log;
	}
	
	/**
	 * Registers the Command
	 * 
	 * @param command The Command to register
	 */
	public final void register(Command command) {
		plugin.getCommandManager().register(command);
	}
	
	/**
	 * Registers the Custom Channel
	 * 
	 * @param channel The Custom Channel to register
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
		plugin.register(listener);
	}
	
	/**
	 * Returns the Addon as a String
	 */
	@Override
	public String toString() {
		return "Addon:" + super.getName();
	}
}