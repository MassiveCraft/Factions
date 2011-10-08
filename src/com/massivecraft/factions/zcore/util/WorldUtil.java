package com.massivecraft.factions.zcore.util;

import java.io.File;

import org.bukkit.Bukkit;

public class WorldUtil
{
	// Previously We had crappy support for multiworld management.
	// This should however be handled by an external plugin!
	/*public static boolean load(String name) {
		if (isWorldLoaded(name)) {
			return true;
		}
		
		if ( ! doesWorldExist(name)) {
			return false;
		}
		
		Environment env = WorldEnv.get(name);
		if (env == null) {
			P.log(Level.WARNING, "Failed to load world. Environment was unknown.");
			return false;
		}
		
		P.p.getServer().createWorld(name, env);
		return true;
	}*/
	
	public static boolean isWorldLoaded(String name)
	{
		return Bukkit.getServer().getWorld(name) != null;
	}
	
	public static boolean doesWorldExist(String name)
	{
		return new File(name, "level.dat").exists();
	}
}
