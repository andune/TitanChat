package com.titankingdoms.nodinchan.titanchat.util.variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.event.chat.MessageFormatEvent;
import com.titankingdoms.nodinchan.titanchat.event.chat.MessageReceiveEvent;
import com.titankingdoms.nodinchan.titanchat.event.chat.MessageSendEvent;
import com.titankingdoms.nodinchan.titanchat.util.variable.VariableHandler.Variable.VarType;

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
 * Variable - Variable manager
 * 
 * @author NodinChan
 *
 */
public final class VariableHandler implements Listener {
	
	private final List<Variable> variables;
	
	public VariableHandler() {
		this.variables = new ArrayList<Variable>();
		TitanChat.getInstance().getServer().getPluginManager().registerEvents(this, TitanChat.getInstance());
	}
	
	/**
	 * Listens to MessageFormatEvent to format with variables
	 * 
	 * @param event MessageFormatEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageFormat(MessageFormatEvent event) {
		for (Variable variable : variables)
			if (event.getClass().isAssignableFrom(variable.getEvent()))
				event.setFormat(variable.replace(event.getFormat(), event.getSender()));
	}
	
	/**
	 * Listens to MessageReceiveEvent to format with variables
	 * 
	 * @param event MessageReceiveEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageReceive(MessageReceiveEvent event) {
		for (Variable variable : variables)
			if (event.getClass().isAssignableFrom(variable.getEvent())) {
				if (variable.getVarType().equals(VarType.FORMAT))
					for (Player recipant : event.getRecipants())
						event.setFormat(recipant, variable.replace(event.getFormat(recipant), event.getSender(), recipant));
				else if (variable.getVarType().equals(VarType.MESSAGE))
					for (Player recipant : event.getRecipants())
						event.setMessage(recipant, variable.replace(event.getMessage(recipant), event.getSender(), recipant));
			}
	}
	
	/**
	 * Listens to MessageSendEvent to format with variables
	 * 
	 * @param event MessageSendEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageSend(MessageSendEvent event) {
		for (Variable variable : variables) {
			if (event.getClass().isAssignableFrom(variable.getEvent())) {
				if (variable.getVarType().equals(VarType.FORMAT))
					event.setFormat(variable.replace(event.getFormat(), event.getSender(), event.getRecipants().toArray(new Player[0])));
				else if (variable.getVarType().equals(VarType.MESSAGE))
					event.setMessage(variable.replace(event.getMessage(), event.getSender(), event.getRecipants().toArray(new Player[0])));
			}
		}
	}
	
	/**
	 * Registers the array of variables
	 * 
	 * @param variables The variables to register
	 */
	public void register(Variable... variables) {
		this.variables.addAll(Arrays.asList(variables));
	}
	
	/**
	 * Unloads this manager
	 */
	public void unload() {
		variables.clear();
	}
	
	/**
	 * IVariable - Represents a variable
	 * 
	 * @author NodinChan
	 *
	 */
	public static abstract class Variable {
		
		/**
		 * Gets the Class of the Event to format for
		 * 
		 * @return The Class of the Event
		 */
		public abstract Class<? extends Event> getEvent();
		
		/**
		 * Gets the replacement for the variable
		 * 
		 * @param sender The sender of the message
		 * 
		 * @param recipants The recipants of the message
		 * 
		 * @return The variable replacement
		 */
		public abstract String getReplacement(Player sender, Player... recipants);
		
		/**
		 * Gets the variable
		 * 
		 * @return The variable
		 */
		public abstract String getVariable();
		
		/**
		 * Gets the variable type of this variable
		 * 
		 * @return The variable Type
		 */
		public abstract VarType getVarType();
		
		/**
		 * Replaces the variable with the replacement
		 * 
		 * @param line The line to be formatted
		 * 
		 * @param sender The sender of the message
		 * 
		 * @param recipants The recipants of the message
		 * 
		 * @return The formattted line
		 */
		public final String replace(String line, Player sender, Player... recipants) {
			return line.replace(getVariable(), getReplacement(sender, recipants));
		}
		
		public enum VarType { FORMAT, MESSAGE }
	}
}