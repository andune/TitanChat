package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class CommandExecutor {
	
	private Method method;
	
	private Command command;
	
	private String name;

	public CommandExecutor(Method method, Command command, String name) {
		this.command = command;
		this.name = name;
	}
	
	public void execute(Player player, String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(command.getClass(), player, args);
	}
	
	public Command getCommand() {
		return command;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public String getName() {
		return name;
	}
}