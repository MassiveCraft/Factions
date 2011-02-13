package com.bukkit.mcteam.factions.util;

import java.util.*;

import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Factions;

public class Log {
	public static String prefix = Factions.factions.getDescription().getName();
	public static ArrayList<Player> debuggers = new ArrayList<Player>();
	public static int threshold = 10;
	
	public static void log(int level, String prefix, String msg) {
		if (threshold <= level) {
			msg = Log.prefix+prefix+msg;
			System.out.println(msg);
			for(Player debugger : debuggers) {
				debugger.sendMessage(msg);
			}
		}
	}
	
	public static void debug (String msg) {
		log(10, " debug:  ", msg);
	}
	
	public static void info (String msg) {
		log(20, " info:   ", msg);
	}
	
	public static void warn (String msg) {
		log(30, " warn:   ", msg);
	}
	
	public static void severe (String msg) {
		log(40, " severe: ", msg);
	}
}
