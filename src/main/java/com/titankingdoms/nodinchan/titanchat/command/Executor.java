package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.command.info.*;

public final class Executor {
	
	private final Method method;
	
	private final CommandBase command;
	
	private final String name;
	private String[] aliases = new String[0];
	private String description = "";
	private String permission = "";
	private String usage = "";
	
	private boolean server = false;
	
	public Executor(CommandBase command, Method method) {
		this.method = method;
		this.command = command;
		this.name = method.getName();
		
		this.server = method.getAnnotation(Command.class).server();
		
		if (method.isAnnotationPresent(Aliases.class))
			this.aliases = method.getAnnotation(Aliases.class).value();
		
		if (method.isAnnotationPresent(Description.class))
			this.description = method.getAnnotation(Description.class).value();
		
		if (method.isAnnotationPresent(Permission.class))
			this.permission = method.getAnnotation(Permission.class).value();
		
		if (method.isAnnotationPresent(Usage.class))
			this.usage = method.getAnnotation(Usage.class).value();
	}
	
	/**
	 * Check if the command can be used on the console
	 * 
	 * @return True if the command can be used on the console
	 */
	public boolean allowServer() {
		return server;
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
	 * @param sender The command sender
	 * 
	 * @param args The command arguments
	 * 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public void execute(CommandSender sender, String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(command, sender, args);
	}
	
	/**
	 * Executes the command
	 * 
	 * @param sender The command sender
	 * 
	 * @param args The command arguments
	 * 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public void executePlayer(Player player, String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
	 * Gets the permission of the command
	 * 
	 * @return The command permission
	 */
	public String getPermission() {
		return permission;
	}
	
	/**
	 * Gets the usage of the command
	 * 
	 * @return The command usage
	 */
	public String getUsage() {
		return usage;
	}
	
	@Override
	public String toString() {
		return "Command:" + name;
	}
}