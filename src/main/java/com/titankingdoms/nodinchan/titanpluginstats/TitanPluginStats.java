package com.titankingdoms.nodinchan.titanpluginstats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public class TitanPluginStats implements Runnable {
	
	private final String info;
	
	private Result result;
	
	public TitanPluginStats(Plugin plugin, String plugin_url) {
		Server server = plugin.getServer();
		String serverInfo = "server_ip=" + server.getIp() + "&" + "server_port=" + server.getPort() + "&" + server.getServerName();
		String pluginInfo = plugin.getDescription().getName() + "&" + plugin.getDescription().getVersion();
		this.info = serverInfo + "&" + pluginInfo + "&" + server.getVersion() + "&plugin_url=" + plugin_url;
	}
	
	public Result getResult() {
		return result;
	}
	
	public void run() {
		try {
			URL url = new URL("http://titankingdoms.com/pstats/pluginstats.php?" + info);
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
			
			urlConnection.disconnect();
			
		} catch (MalformedURLException e) { this.result = Result.FAILURE; } catch (IOException e) { this.result = Result.FAILURE; }
	}
	
	public enum Result {
		FAILURE,
		SUCCESS
	}
}