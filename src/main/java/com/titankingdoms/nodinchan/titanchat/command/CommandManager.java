package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.command.commands.*;
import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

/**
 * CommandManager - Manages registered commands
 * 
 * @author NodinChan
 *
 */
public final class CommandManager {
	
	private final TitanChat plugin;
	
	private final CommandLoader loader;
	
	private static final Debugger db = new Debugger(4);
	
	private List<CommandExecutor> executors;
	
	public CommandManager(TitanChat plugin) {
		this.plugin = plugin;
		this.executors = new ArrayList<CommandExecutor>();
		this.loader = new CommandLoader(plugin);
	}
	
	/**
	 * Searches for the command and executes it if found
	 * 
	 * @param player The command sender
	 * 
	 * @param command The command
	 * 
	 * @param args The arguments
	 */
	public void execute(Player player, String command, String[] args) {
		for (CommandExecutor executor : executors) {
			if (executor.getMethod().getAnnotation(CommandID.class).requireChannel() && !plugin.enableChannels()) {
				plugin.sendWarning(player, "This command requires channels to be enabled");
				return;
			}
			
			for (String trigger : executor.getMethod().getAnnotation(CommandID.class).triggers()) {
				db.i("Checking trigger \"" + trigger + "\" with command \"" + command + "\"");
				
				if (trigger.equalsIgnoreCase(command))
					try {
						executor.execute(player, args);
						return;
					} catch (IllegalAccessException e) {
						db.i("An IllegalAccessException has occured while using command: " + executor.getName());
						db.i(e.getLocalizedMessage());
						break;
					} catch (IllegalArgumentException e) {
						db.i("An IllgealArgumentException has occured while using command: " + executor.getName());
						db.i(e.getLocalizedMessage());
						break;
					} catch (InvocationTargetException e) {
						db.i("An InvocationTargetException has occured while using command: " + executor.getName());
						db.i(e.getLocalizedMessage());
						break;
					}
			}
		}
		
		plugin.sendWarning(player, "Invalid Command");
		plugin.sendInfo(player, "\"/titanchat commands [page]\" for command list");
	}
	
	/**
	 * Gets the amount of commands
	 * 
	 * @return The amount of commands
	 */
	public int getCommandAmount() {
		return executors.size();
	}
	
	/**
	 * Gets the CommandExecutor by its name
	 * 
	 * @param name The name
	 * 
	 * @return The CommandExecutor if it exists, otherwise null
	 */
	public CommandExecutor getCommandExecutor(String name) {
		for (CommandExecutor executor : executors) {
			if (executor.getName().equals(name))
				return executor;
		}
		
		return null;
	}
	
	/**
	 * Gets the CommandExecutor from the list by index
	 * 
	 * @param exeNum The index of the executor in the list
	 * 
	 * @return The CommandExecutor
	 */
	public CommandExecutor getCommandExecutor(int exeNum) {
		return executors.get(exeNum);
	}
	
	/**
	 * Loads all Commands
	 */
	public void load() {
		register(new AdministrateCommand());
		register(new ChannelCommand());
		register(new ChatCommand());
		register(new InformationCommand());
		register(new InvitationCommand());
		register(new PluginCommand());
		register(new RankingCommand());
		register(new SettingsCommand());
		
		for (Command command : loader.load()) { register(command); }
		
		sortCommands();
	}
	
	/**
	 * Registers the Command
	 * 
	 * @param command The Command to be registered
	 */
	public void register(Command command) {
		db.i("Try to register command " + command.toString());
		
		for (Method method : command.getClass().getMethods()) {
			if (method.getAnnotation(CommandID.class) != null) {
				db.i("Adding new executor: " + method.getAnnotation(CommandID.class).name());
				executors.add(new CommandExecutor(method, command, method.getAnnotation(CommandID.class).name()));
			}
		}
	}
	
	/**
	 * Sorts the Commands
	 */
	public void sortCommands() {
		List<CommandExecutor> executors = new ArrayList<CommandExecutor>();
		List<String> names = new ArrayList<String>();
		
		for (CommandExecutor executor : this.executors) {
			names.add(executor.getName());
		}
		
		Collections.sort(names);
		
		for (String name: names) {
			executors.add(getCommandExecutor(name));
		}
		
		this.executors = executors;
	}
	
	/**
	 * Unloads the Commands
	 */
	public void unload() {
		executors.clear();
	}
}