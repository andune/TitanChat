package com.titankingdoms.nodinchan.titanchat.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * Debugger - Provides methods for logging when in debug mode
 * 
 * @author NodinChan, SLiPCoR
 *
 */
public final class Debugger {
	
	private static final Logger log = Logger.getLogger("TitanDebug");
	
	private final int id;
	
	private static final Set<Integer> debug = new HashSet<Integer>();
	
	public Debugger(int id) {
		this.id = id;
	}
	
	/**
	 * Disables the debugger
	 */
	public void disable() {
		debug.remove(id);
	}
	
	/**
	 * Disables the debugger
	 * 
	 * @param id The debugger ID
	 */
	public static void disable(int id) {
		debug.remove(id);
	}
	
	/**
	 * Disables the debugger
	 * 
	 * @param debugId The type to disable
	 */
	public static void disable(String debugId) {
		if (debugId.equalsIgnoreCase("none"))
			return;
		
		try {
			int id = Integer.parseInt(debugId);
			debug.remove(id);
			
		} catch (NumberFormatException e) {
			if (debugId.equalsIgnoreCase("all"))
				debug.remove(1024);
		}
	}
	
	/**
	 * Enables the debugger
	 */
	public void enable() {
		debug.add(id);
	}
	
	/**
	 * Enables the debugger
	 * 
	 * @param id The debugger id
	 */
	public static void enable(int id) {
		debug.add(id);
	}
	
	/**
	 * Enables the debugger
	 * 
	 * @param debugId The debugger type
	 */
	public static void enable(String debugId) {
		if (debugId.equalsIgnoreCase("none"))
			return;
		
		try {
			int id = Integer.parseInt(debugId);
			debug.add(id);
			
		} catch (NumberFormatException e) {
			if (debugId.equalsIgnoreCase("all"))
				debug.add(1024);
		}
	}
	
	/**
	 * Info debugger
	 * 
	 * @param message The debug message
	 */
	public void i(String message) {
		if (isDebugging())
			log(Level.INFO, message);
	}
	
	/**
	 * Check if the debugger is debugging
	 * 
	 * @return True if the debugger is debugging
	 */
	public boolean isDebugging() {
		return debug.contains(id) || debug.contains(1024);
	}
	
	/**
	 * Check if the debugger is debugging
	 * 
	 * @param id The debugger id
	 * 
	 * @return True if the debugger is debugging
	 */
	public static boolean isDebugging(int id) {
		return debug.contains(id) || debug.contains(1024);
	}
	
	/**
	 * Loads the debugger
	 * 
	 * @param configOption The config option
	 */
	public static void load(String configOption) {
		if (configOption.equalsIgnoreCase("none"))
			return;
		
		if (configOption.contains(","))
			for (String id : configOption.split(","))
				enable(id.trim());
		else
			enable(configOption);
	}
	
	/**
	 * Sends the message to the log
	 * 
	 * @param level Level of the announcement
	 * 
	 * @param msg The message to send
	 */
	public void log(Level level, String msg) {
		log.log(level, "[TitanDebug] " + msg);
	}
	
	/**
	 * Severe debugger
	 * 
	 * @param message The debug message
	 */
	public void s(String message) {
		if (isDebugging())
			log(Level.SEVERE, message);
	}
	
	/**
	 * Warning debugger
	 * 
	 * @param message The debug message
	 */
	public void w(String message) {
		if (isDebugging())
			log(Level.WARNING, message);
	}
}