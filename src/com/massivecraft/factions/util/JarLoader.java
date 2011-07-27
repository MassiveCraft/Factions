package com.massivecraft.factions.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JarLoader {
	
	private static URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
	
	public static boolean load(String filename) {
		return load(new File(filename));
	}
	
	public static boolean load(File file) {
		if ( ! file.exists()) {
			log("This file does not exist: " + file);
			return false;
		}
		
		try {
			return load(file.toURI().toURL());
		} catch (MalformedURLException e) {
			log("The url for \""+file+"\" was malformed." + e);
			return false;
		}
	}
	
	public static boolean load(URL url) {
		// If the file already is loaded we can skip it
		for (URL otherUrl : sysloader.getURLs()) {
			if (otherUrl.sameFile(url)) {
				return true;
			}
		}
		
		try {
			Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{ URL.class });
			addURLMethod.setAccessible(true);
			addURLMethod.invoke(sysloader, new Object[]{ url });
			return true;
		} catch (Exception e) {
			log("Failed to load \""+url+"\":" + e);
			return false;
		}
	}
	
	// -------------------------------------------- //
	// Logger
	// -------------------------------------------- //
	private static void log(Object o) {
		Logger.getLogger("Minecraft").log(Level.SEVERE, "[JAR-LOADER] " + o);
	}
	
}