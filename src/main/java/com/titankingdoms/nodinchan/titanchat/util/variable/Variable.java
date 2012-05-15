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
import com.titankingdoms.nodinchan.titanchat.events.MessageFormatEvent;
import com.titankingdoms.nodinchan.titanchat.events.MessageReceiveEvent;
import com.titankingdoms.nodinchan.titanchat.events.MessageSendEvent;

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
public final class Variable implements Listener {
	
	private final List<IVariable> variables;
	
	public Variable() {
		this.variables = new ArrayList<IVariable>();
		TitanChat.getInstance().getServer().getPluginManager().registerEvents(this, TitanChat.getInstance());
	}
	
	/**
	 * Listens to MessageFormatEvent to format with variables
	 * 
	 * @param event MessageFormatEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageFormat(MessageFormatEvent event) {
		for (IVariable variable : variables)
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
		for (IVariable variable : variables)
			if (event.getClass().isAssignableFrom(variable.getEvent()))
				event.setFormat(variable.replace(event.getFormat(), event.getSender(), event.getRecipant()));
	}
	
	/**
	 * Listens to MessageSendEvent to format with variables
	 * 
	 * @param event MessageSendEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageSend(MessageSendEvent event) {
		for (IVariable variable : variables)
			if (event.getClass().isAssignableFrom(variable.getEvent()))
				event.setMessage(variable.replace(event.getMessage(), event.getSender(), event.getRecipants().toArray(new Player[0])));
	}
	
	/**
	 * Registers the array of variables
	 * 
	 * @param variables The variables to register
	 */
	public void register(IVariable... variables) {
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
	public static abstract class IVariable {
		
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
	}
}