package com.titankingdoms.nodinchan.titanchat.debug;

import java.util.HashSet;

import org.bukkit.Bukkit;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

/**
 * Debugger Class
 * 
 * -
 * 
 * Provides methods for logging when in debug mode
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
	 * @param i
	 *            the debug id to check
	 */
	public Debugger(int i) {
		id = i;
	}
	
	/**
	 * Check if debugging enabled
	 * 
	 * @return true if debug is enabled
	 */
	private boolean debugs() {
		return override || check.contains(id) || check.contains(666);
	}
	
	/**
	 * Log a message prefixed with INFO
	 * 
	 * @param s
	 *            the message
	 */
	public void i(String s) {
		if (!debugs() || level < 1)
			return;
		Bukkit.getLogger().info(prefix + s);
	}
	
	/**
	 * Log a message prefixed with WARNING
	 * 
	 * @param s
	 *            the message
	 */
	public void w(String s) {
		if (!debugs() || level < 2)
			return;
		Bukkit.getLogger().warning(prefix + s);
	}
	
	/**
	 * Log a message prefixed with SEVERE
	 * 
	 * @param s
	 *            the message
	 */
	public void s(String s) {
		if (!debugs() || level < 3)
			return;
		Bukkit.getLogger().severe(prefix + s);
	}
	
	/**
	 * Read a string array and return a readable string
	 * 
	 * @param s
	 *            the string array
	 *            
	 * @return a string, the array elements joined with comma
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
	
	public static void enable(int debug) {
		level = (byte) debug;
	}
	
	public static void enableAll() {
		Debugger.check.add(666);
	}
	
	public static void disable() {
		Debugger.override = false;
		check.clear();
	}
}

// debug ids:
// 1 - titanchat
// 2 - .addon
// 3 - .channel
// 4 - .command
// 5 - .permissions

