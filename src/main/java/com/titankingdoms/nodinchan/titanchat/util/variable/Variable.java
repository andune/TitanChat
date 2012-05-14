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

public final class Variable implements Listener {
	
	private final List<IVariable> variables;
	
	public Variable() {
		this.variables = new ArrayList<IVariable>();
		TitanChat.getInstance().getServer().getPluginManager().registerEvents(this, TitanChat.getInstance());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageFormat(MessageFormatEvent event) {
		for (IVariable variable : variables)
			if (event.getClass().isAssignableFrom(variable.getEvent()))
				event.setFormat(variable.replace(event.getFormat(), event.getSender()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageReceive(MessageReceiveEvent event) {
		for (IVariable variable : variables)
			if (event.getClass().isAssignableFrom(variable.getEvent()))
				event.setFormat(variable.replace(event.getFormat(), event.getSender(), event.getRecipant()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageSend(MessageSendEvent event) {
		for (IVariable variable : variables)
			if (event.getClass().isAssignableFrom(variable.getEvent()))
				event.setMessage(variable.replace(event.getMessage(), event.getSender(), event.getRecipants().toArray(new Player[0])));
	}
	
	public void register(IVariable... variables) {
		this.variables.addAll(Arrays.asList(variables));
	}
	
	public void unload() {
		variables.clear();
	}
	
	public static abstract class IVariable {
		
		public abstract Class<? extends Event> getEvent();
		
		public abstract String getReplacement(Player sender, Player... recipants);
		
		public abstract String getVariable();
		
		public final String replace(String line, Player sender, Player... recipants) {
			return line.replace(getVariable(), getReplacement(sender, recipants));
		}
	}
}