package com.titankingdoms.nodinchan.titanchat.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;

public class SupportLoader {
	
	private TitanChat plugin;
	
	private File channelDir;
	private File supportDir;
	
	private ClassLoader channelLoader;
	private ClassLoader supportLoader;
	
	private List<File> channelFiles;
	private List<File> supportFiles;
	private List<CustomChannel> channels;
	private List<Support> supports;
	
	private Map<String, String> ccJarNames;
	private Map<String, String> paJarNames;
	
	public SupportLoader(TitanChat plugin) {
		this.plugin = plugin;
		channelFiles = new ArrayList<File>();
		supportFiles = new ArrayList<File>();
		channels = new ArrayList<CustomChannel>();
		supports = new ArrayList<Support>();
		channelDir = plugin.getChannelsFolder();
		supportDir = plugin.getSupportsFolder();
		ccJarNames = new HashMap<String, String>();
		paJarNames = new HashMap<String, String>();
		
		List<URL> channelUrls = new ArrayList<URL>();
		List<URL> supportUrls = new ArrayList<URL>();
		
		for (String channelFile : channelDir.list()) {
			if (channelFile.endsWith(".jar")) {
				File file = new File(channelDir, channelFile);
				
				channelFiles.add(file);
				try {
					channelUrls.add(file.toURI().toURL());
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		
		for (String supportFile : supportDir.list()) {
			if (supportFile.endsWith(".jar")) {
				File file = new File(supportDir, supportFile);
				
				supportFiles.add(file);
				try {
					supportUrls.add(file.toURI().toURL());
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		
		channelLoader = URLClassLoader.newInstance(channelUrls.toArray(new URL[channelUrls.size()]), plugin.getClass().getClassLoader());
		supportLoader = URLClassLoader.newInstance(supportUrls.toArray(new URL[supportUrls.size()]), plugin.getClass().getClassLoader());
	}
	
	public String getCustomChannelJar(String name) {
		return ccJarNames.get(name);
	}
	
	public String getPluginAddonJar(String name) {
		return paJarNames.get(name);
	}
	
	public List<CustomChannel> loadChannels() throws Exception {
		for (File file : channelFiles) {
			try {
				JarFile jarFile = new JarFile(file);
				Enumeration<JarEntry> entries = jarFile.entries();
				
				String mainClass = null;
				
				while (entries.hasMoreElements()) {
					JarEntry element = entries.nextElement();
					
					if (element.getName().equalsIgnoreCase("path.yml")) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
						mainClass = reader.readLine().substring(12);
						break;
					}
				}
				
				if (mainClass != null) {
					Class<?> clazz = Class.forName(mainClass, true, channelLoader);
					Class<? extends CustomChannel> channelClass = clazz.asSubclass(CustomChannel.class);
					Constructor<? extends CustomChannel> ctor = channelClass.getConstructor(plugin.getClass());
					CustomChannel channel = ctor.newInstance(plugin);
					ccJarNames.put(channel.getName(), file.getName());
					channel.init();
					channels.add(channel);
					
				} else {
					throw new Exception();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				plugin.log(Level.WARNING, "The custom channel " + file.getName() + " failed to load");
			}
		}
		
		if (!channels.isEmpty()) {
			StringBuilder str = new StringBuilder();
			
			for (CustomChannel channel : channels) {
				if (str.length() > 0)
					str.append(", ");
				
				str.append(channel.getName());
			}
			
			plugin.log(Level.INFO, "Loaded custom channels: " + str.toString());
		}
		
		return channels;
	}
	
	public List<Support> loadSupports() throws Exception {
		for (File file : supportFiles) {
			try {
				JarFile jarFile = new JarFile(file);
				Enumeration<JarEntry> entries = jarFile.entries();
				
				String mainClass = null;
				
				while (entries.hasMoreElements()) {
					JarEntry element = entries.nextElement();
					
					if (element.getName().equalsIgnoreCase("path.yml")) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
						mainClass = reader.readLine().substring(12);
						break;
					}
				}
				
				if (mainClass != null) {
					Class<?> clazz = Class.forName(mainClass, true, supportLoader);
					Class<? extends Support> supportClass = clazz.asSubclass(Support.class);
					Constructor<? extends Support> ctor = supportClass.getConstructor(plugin.getClass());
					Support support = ctor.newInstance(plugin);
					paJarNames.put(support.getName(), file.getName());
					support.init();
					supports.add(support);
					
				} else {
					throw new Exception();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				plugin.log(Level.WARNING, "The plugin support " + file.getName() + " failed to load");
			}
		}
		
		if (!supports.isEmpty()) {
			StringBuilder str = new StringBuilder();
			
			for (Support support : supports) {
				if (str.length() > 0)
					str.append(", ");
				
				str.append(support.getName());
			}
			
			plugin.log(Level.INFO, "Loaded plugin supports: " + str.toString());
		}
		
		return supports;
	}
}