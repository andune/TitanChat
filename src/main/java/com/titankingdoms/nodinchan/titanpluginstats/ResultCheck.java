package com.titankingdoms.nodinchan.titanpluginstats;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitWorker;

import com.titankingdoms.nodinchan.titanpluginstats.TitanPluginStats.Result;

public class ResultCheck implements Runnable {
	
	private final Plugin plugin;
	
	private final BukkitScheduler scheduler;
	
	private BukkitWorker worker;
	
	public ResultCheck(Plugin plugin, int taskID) {
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
		for (BukkitWorker worker : scheduler.getActiveWorkers()) {
			if (!worker.getOwner().equals(plugin) || worker.getTaskId() != taskID)
				continue;
			
			this.worker = worker;
		}
	}
	
	public void run() {
		if (worker == null)
			return;
		
		if (worker instanceof TitanPluginStats) {
			if (((TitanPluginStats) worker).getResult().equals(Result.FAILURE)) {
				scheduler.cancelTask(worker.getTaskId());
				scheduler.scheduleAsyncRepeatingTask(plugin, new TitanPluginStats(plugin), 6000, 108000);
			}
		}
	}
}