package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.command.commands.*;

public class CommandManager {
	
	private TitanChat plugin;
	
	private List<CommandExecutor> executors;
	
	public CommandManager(TitanChat plugin) {
		this.plugin = plugin;
		this.executors = new ArrayList<CommandExecutor>();
	}
	
	public void execute(Player player, String command, String[] args) {
		for (CommandExecutor executor : executors) {
			if (executor.getMethod().getAnnotation(CommandID.class).requireChannel() && !plugin.enableChannels()) {
				plugin.sendWarning(player, "This command requires channels to be enabled");
				return;
			}
			
			for (String trigger : executor.getMethod().getAnnotation(CommandID.class).triggers()) {
				if (trigger.equalsIgnoreCase(command))
					try { executor.execute(player, args); return; } catch (IllegalAccessException e) {} catch (IllegalArgumentException e) {} catch (InvocationTargetException e) {}
			}
		}
		
		plugin.sendWarning(player, "Invalid Command");
		plugin.sendInfo(player, "'/titanchat commands [page]' for command list");
	}
	
	public int getCommandAmount() {
		return executors.size();
	}
	
	public CommandExecutor getCommandExecutor(String name) {
		for (CommandExecutor executor : executors) {
			if (executor.getName().equals(name))
				return executor;
		}
		
		return null;
	}
	
	public CommandExecutor getCommandExecutor(int exeNum) {
		return executors.get(exeNum);
	}
	
	public void load() {
		register(new AdministrateCommand(plugin));
		register(new ChannelCommand(plugin));
		register(new ChatCommand(plugin));
		register(new InformationCommand(plugin));
		register(new InvitationCommand(plugin));
		register(new RankingCommand(plugin));
		register(new ReloadCommand(plugin));
		register(new SettingsCommand(plugin));
		
		try { for (Command command : plugin.getLoader().loadCommands()) { register(command); } } catch (Exception e) {}
		
		sortCommands();
	}
	
	public void register(Command command) {
		for (Method method : command.getClass().getMethods()) {
			if (method.getAnnotation(CommandID.class) != null) {
				executors.add(new CommandExecutor(method, command, method.getAnnotation(CommandID.class).name()));
			}
		}
	}
	
	public List<CommandExecutor> sortCommands() {
		List<CommandExecutor> executors = new ArrayList<CommandExecutor>();
		List<String> names = new ArrayList<String>();
		
		for (CommandExecutor executor : this.executors) {
			names.add(executor.getName());
		}
		
		Collections.sort(names);
		
		for (String name: names) {
			executors.add(getCommandExecutor(name));
		}
		
		return executors;
	}
}