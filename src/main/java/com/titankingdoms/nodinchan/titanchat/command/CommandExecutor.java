package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

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
 * CommandExecutor - Stores commands and executes them when needed
 * 
 * @author NodinChan
 *
 */
public final class CommandExecutor {

	private final Method method;
	
	private final Command command;
	
	private final String name;
	
	/**
	 * Initialises variables
	 * 
	 * @param method The method of the command
	 * 
	 * @param command The Command instance with the method
	 * 
	 * @param name The name of the Command
	 */
	public CommandExecutor(Method method, Command command, String name) {
		this.method = method;
		this.command = command;
		this.name = name;
	}
	
	/**
	 * Check if a CommandExecutor equals another
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof CommandExecutor)
			if (((CommandExecutor) object).getCommand().equals(getCommand()))
				if (((CommandExecutor) object).getMethod().equals(getMethod()))
					return ((CommandExecutor) object).getName().equals(getName());
		
		return false;
	}
	
	/**
	 * Executes the command
	 * 
	 * @param player The command sender
	 * 
	 * @param args The arguments
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
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
	 * Gets the Method
	 * 
	 * @return The Method
	 */
	public Method getMethod() {
		return method;
	}
	
	/**
	 * Gets the name of the command
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the CommandExecutor as a String
	 */
	@Override
	public String toString() {
		return "Command:" + name;
	}
}