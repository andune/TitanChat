package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nodinchan.nclib.loader.Loadable;
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
	
	/**
	 * Initialises variables
	 */
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
		Executor executor = plugin.getCommandManager().getCommandExecutor(name);
		
		if (executor.getMethod().getAnnotation(CommandInfo.class) != null)
			plugin.sendInfo(player, "Usage: /titanchat " + executor.getMethod().getAnnotation(CommandInfo.class).usage());
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
		plugin.register(listener);
	}
	
	/**
	 * Executor - Represents each command method in a Command
	 * 
	 * @author NodinChan
	 *
	 */
	public static final class Executor {
		
		private final Method method;
		
		private final Command command;
		
		private final String name;
		
		public Executor(Method method, Command command) {
			this.method = method;
			this.command = command;
			this.name = method.getAnnotation(CommandID.class).name();
		}
		
		@Override
		public boolean equals(Object object) {
			if (object instanceof Executor)
				if (((Executor) object).getMethod().equals(method))
					if (((Executor) object).getCommand().equals(command))
						if (((Method) object).getName().equals(name))
							return true;
			
			return false;
		}
		
		/**
		 * Executes the command
		 * 
		 * @param player The command sender
		 * 
		 * @param args The command arguments
		 * 
		 * @throws InvocationTargetException 
		 * @throws IllegalArgumentException 
		 * @throws IllegalAccessException 
		 */
		public void execute(Player player, String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			method.invoke(command, player, args);
		}
		
		/**
		 * Gets the Command
		 * 
		 * @return The Command
		 */
		public Command getCommand() {
			return command;
		}
		
		/**
		 * Gets the method
		 * 
		 * @return The Method
		 */
		public Method getMethod() {
			return method;
		}
		
		/**
		 * Gets the name of the command
		 * 
		 * @return The command name
		 */
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return "Command:" + name;
		}
	}
}