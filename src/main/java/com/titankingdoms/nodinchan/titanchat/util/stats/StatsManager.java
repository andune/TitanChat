package com.titankingdoms.nodinchan.titanchat.util.stats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.titankingdoms.nodinchan.titanchat.event.MessageSendEvent;

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

public final class StatsManager implements Listener {
	
	private long chars = 0;
	private long lines = 0;
	private long words = 0;
	
	public long getCharacters() {
		return chars;
	}
	
	public long getLines() {
		return lines;
	}
	
	public long getWords() {
		return words;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMessageSend(MessageSendEvent event) {
		this.chars += event.getMessage().toCharArray().length;
		this.lines++;
		this.words += event.getMessage().split(" ").length;
	}
}