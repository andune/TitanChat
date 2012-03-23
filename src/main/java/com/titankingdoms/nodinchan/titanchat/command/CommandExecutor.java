package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

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