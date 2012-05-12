package com.titankingdoms.nodinchan.titanchat.util.variable;

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
	
	public Variable() {
		TitanChat.getInstance().getServer().getPluginManager().registerEvents(this, TitanChat.getInstance());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageFormat(MessageFormatEvent event) {
		for (IVariable variable : FormatVariable.getVariables())
			event.setFormat(variable.replace(event.getFormat()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageReceive(MessageReceiveEvent event) {
		for (IVariable variable : ReceiveVariable.getVariables())
			event.setFormat(variable.replace(event.getFormat()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageSend(MessageSendEvent event) {
		for (IVariable variable : SendVariable.getVariables())
			event.setMessage(variable.replace(event.getMessage()));
	}
	
	public static void unload() {
		FormatVariable.unload();
		ReceiveVariable.unload();
		SendVariable.unload();
	}
	
	public interface IVariable {
		
		public String getReplacement();
		
		public String getVariable();
		
		public String replace(String line);
	}
}