package com.titankingdoms.nodinchan.titanchat.util;

import java.util.HashSet;

import org.bukkit.Bukkit;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

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
 * @author slipcor
 * 
 */
public class Debugger {
	
	private int id = 0;
	
	private static byte level = 3;
	
	private static String prefix = "[TC-Debug] ";
	
	public static boolean override = false;
	
	private static HashSet<Integer> check = new HashSet<Integer>();
	
	/**
	 * Debug constructor
	 * 
	 * @param i The debug id to check
	 */
	public Debugger(int id) {
		this.id = id;
	}
	
	/**
	 * Check if debugging enabled
	 * 
	 * @return True if debug is enabled
	 */
	private boolean debugs() {
		return override || check.contains(id) || check.contains(666);
	}
	
	/**
	 * Disables all debuggers
	 */
	public static void disable() {
		Debugger.override = false;
		check.clear();
	}
	/**
	 * Enables a particular debugger
	 * 
	 * @param debug Debugger id
	 */
	public static void enable(String debug) {
		try {
			check.add(Integer.valueOf(debug));
			System.out.println("Debugging: " + debug);
		} catch (NumberFormatException e) {
			System.out.println("Debug load error: " + debug);
		}
		
		if (debug.equals("i"))
			level = (byte) 1;
		else if (debug.equals("w"))
			level = (byte) 2;
		else if (debug.equals("s"))
			level = (byte) 3;
	}
	
	/**
	 * Enables all debuggers
	 */
	public static void enableAll() {
		Debugger.check.add(666);
	}

	/**
	 * Reads a string array and return a readable string
	 * 
	 * @param s The string array
	 *            
	 * @return A string, the array elements joined with comma
	 */
	public String formatStringArray(String[] s) {
		if (s == null)
			return "NULL";
		String result = "";
		for (int i = 0; i < s.length; i++) {
			result = result + (result.equals("") ? "" : ",") + s[i];
		}
		return result;
	}

	/**
	 * Logs a message prefixed with INFO
	 * 
	 * @param s The message
	 */
	public void i(String s) {
		if (!debugs() || level < 1)
			return;
		Bukkit.getLogger().info(prefix + s);
	}
	
	/**
	 * Check if Debugger is debugging
	 * 
	 * @return True if debugging
	 */
	public boolean isDebugging() {
		return debugs();
	}
	
	/**
	 * Loads the Debugger
	 * 
	 * @param instance TitanChat
	 */
	public static void load(TitanChat instance) {
		String debugs = instance.getConfig().getString("debug", "none");
		
		if (!debugs.equals("none")) {
			if (debugs.equals("all") || debugs.equals("full")) {
				Debugger.check.add(666);
				System.out.print("Debugging EVERYTHING");
			} else {
				String[] sIds = debugs.split(",");
				for (String s : sIds) {
					try {
						Debugger.check.add(Integer.valueOf(s));
						System.out.print("Debugging: " + s);
					} catch (Exception e) {
						System.out.print("Debug load error: " + s);
					}
					if (s.equals("i")) {
						level = (byte) 1;
					} else if (s.equals("w")) {
						level = (byte) 2;
					} else if (s.equals("s")) {
						level = (byte) 3;
					}
				}
			}
		}
	}

	/**
	 * Logs a message prefixed with SEVERE
	 * 
	 * @param s The message
	 */
	public void s(String s) {
		if (!debugs() || level < 3)
			return;
		Bukkit.getLogger().severe(prefix + s);
	}

	/**
	 * Logs a message prefixed with WARNING
	 * 
	 * @param s The message
	 */
	public void w(String s) {
		if (!debugs() || level < 2)
			return;
		Bukkit.getLogger().warning(prefix + s);
	}
}

// debug ids:
// 1 - titanchat
// 2 - .addon
// 3 - .channel
// 4 - .command
// 5 - .permissions

