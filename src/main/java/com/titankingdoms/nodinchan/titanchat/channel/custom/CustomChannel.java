package com.titankingdoms.nodinchan.titanchat.channel.custom;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;

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

public class CustomChannel extends Channel {
	
	public CustomChannel(String name) {
		super(name, Option.CUSTOM);
	}
	
	@Override
	public boolean access(Player player) {
		return true;
	}
	
	@Override
	public final Channel create(CommandSender sender, String name, Option option) {
		return this;
	}

	@Override
	public final String getType() {
		return "Custom";
	}

	@Override
	public final Channel load(String name, Option option) {
		return this;
	}
}