package com.titankingdoms.nodinchan.titanpluginstats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.plugin.Plugin;

public class TitanPluginStats implements Runnable {
	
	private final String server_ip;
	private final String server_port;
	private final String server_name;
	private final String plugin_name;
	private final String plugin_version;
	private final String cb_version;
	
	private Result result;
	
	public TitanPluginStats(Plugin plugin) {
		this.server_ip = "server_ip=" + plugin.getServer().getIp();
		this.server_port = "server_port=" + plugin.getServer().getPort();
		this.server_name = "server_name=" + plugin.getServer().getServerName();
		this.plugin_name = "plugin_name=" + plugin.getDescription().getName();
		this.plugin_version = "plugin_version=" + plugin.getDescription().getVersion();
		this.cb_version = "cb_version=" + plugin.getServer().getVersion();
	}
	
	public Result getResult() {
		return result;
	}
	
	public void run() {
		try {
			URL url = new URL("http://titankingdoms.com/pstats/pluginstats.php?" + server_ip + "&" + server_port + "&" + server_name + "&" + plugin_name + "&" + plugin_version + "&" + cb_version);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			
			if (reader.ready()) {
				String line = reader.readLine();
				
				if (line == null || line.equals("FAILED"))
					this.result = Result.FAILURE;
				else if (line.equals("SUCCESS"))
					this.result = Result.SUCCESS;
			}
			
		} catch (MalformedURLException e) {} catch (IOException e) {}
	}
	
	public enum Result {
		FAILURE,
		SUCCESS
	}
}