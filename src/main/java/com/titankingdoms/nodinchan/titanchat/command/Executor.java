package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.command.info.Aliases;
import com.titankingdoms.nodinchan.titanchat.command.info.ChCommand;
import com.titankingdoms.nodinchan.titanchat.command.info.Description;
import com.titankingdoms.nodinchan.titanchat.command.info.Usage;

public final class Executor {
	
	private final Method method;
	
	private final CommandBase command;
	
	private final String name;
	private String[] aliases = new String[0];
	private String description = "";
	private String usage = "";
	
	private boolean channel = false;
	
	public Executor(CommandBase command, Method method) {
		this.method = method;
		this.command = command;
		this.name = method.getName();
		
		if (method.isAnnotationPresent(ChCommand.class))
			this.channel = true;
		
		if (method.isAnnotationPresent(Aliases.class))
			this.aliases = method.getAnnotation(Aliases.class).value();
		
		if (method.isAnnotationPresent(Description.class))
			this.description = method.getAnnotation(Description.class).value();
		
		if (method.isAnnotationPresent(Usage.class))
			this.usage = method.getAnnotation(Usage.class).value();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Executor)
			if (((Executor) object).method.equals(method))
				if (((Executor) object).command.equals(command))
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
	 * Gets the aliases of the command
	 * 
	 * @return The command aliases
	 */
	public String[] getAliases() {
		return aliases;
	}
	
	/**
	 * Gets the Command
	 * 
	 * @return The Command
	 */
	public CommandBase getCommand() {
		return command;
	}
	
	/**
	 * Gets the description of the command
	 * 
	 * @return The command description
	 */
	public String getDescription() {
		return description;
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
	
	/**
	 * Gets the usage of the command
	 * 
	 * @return The command usage
	 */
	public String getUsage() {
		return usage;
	}
	
	/**
	 * Check if the command requires channels to be enabled
	 * 
	 * @return True if channels have to be enabled
	 */
	public boolean requireChannel() {
		return channel;
	}
	
	@Override
	public String toString() {
		return "Command:" + name;
	}
}