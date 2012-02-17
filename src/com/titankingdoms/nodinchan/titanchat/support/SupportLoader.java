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
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class SupportLoader {
	
	private TitanChat plugin;
	
	private File dir;
	
	private ClassLoader loader;

	private List<File> files;
	private List<TCSupport> supports;
	
	public SupportLoader(TitanChat plugin) {
		this.plugin = plugin;
		files = new ArrayList<File>();
		supports = new ArrayList<TCSupport>();
		dir = new File(plugin.getDataFolder(), "supports");
		dir.mkdir();
		
		List<URL> urls = new ArrayList<URL>();
		for (String supportFile : dir.list()) {
			if (supportFile.endsWith(".jar")) {
				File file = new File(dir, supportFile);
				
				files.add(file);
				try {
					urls.add(file.toURI().toURL());
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		
		loader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), plugin.getClass().getClassLoader());
	}
	
	public List<TCSupport> load() throws Exception {
		for (File file : files) {
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
					Class<?> clazz = Class.forName(mainClass, true, loader);
					Class<? extends TCSupport> supportClass = clazz.asSubclass(TCSupport.class);
					Constructor<? extends TCSupport> ctor = supportClass.getConstructor(plugin.getClass());
					TCSupport support = ctor.newInstance(plugin);
					support.init();
					supports.add(support);
					
					plugin.log(Level.INFO, "Loaded plugin support: " + file.getName().replace(".jar", ""));
					
				} else {
					throw new Exception();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				plugin.log(Level.INFO, "The plugin support " + file.getName() + " failed to load");
			}
		}
		
		return supports;
	}
}
