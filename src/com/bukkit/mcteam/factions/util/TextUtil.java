package com.bukkit.mcteam.factions.util;
import java.util.*;

import org.bukkit.Material;

import com.bukkit.mcteam.factions.entities.*;

public class TextUtil {
	public static String titleize(String str) {
		String line = Conf.colorChrome+repeat("_", 60);
		String center = ".[ " + Conf.colorSystem + str + Conf.colorChrome + " ].";
		int pivot = line.length() / 2;
		int eatLeft = center.length() / 2;
		int eatRight = center.length() - eatLeft;
		return line.substring(0, pivot - eatLeft) + center + line.substring(pivot + eatRight);
	}
	
	public static String repeat(String s, int times) {
	    if (times <= 0) return "";
	    else return s + repeat(s, times-1);
	}
	
	public static ArrayList<String> split(String str) {
		return new ArrayList<String>(Arrays.asList(str.trim().split("\\s+")));
	}
	
	public static String implode(List<String> list, String glue) {
	    String ret = "";
	    for (int i=0; i<list.size(); i++) {
	        if (i!=0) {
	        	ret += glue;
	        }
	        ret += list.get(i);
	    }
	    return ret;
	}
	public static String implode(List<String> list) {
		return implode(list, " ");
	}
		
	public static String commandHelp(List<String> aliases, String param, String desc) {
		ArrayList<String> parts = new ArrayList<String>();
		parts.add(Conf.colorCommand+Conf.aliasBase.get(0));
		parts.add(TextUtil.implode(aliases, ", "));
		if (param.length() > 0) {
			parts.add(Conf.colorParameter+param);
		}
		if (desc.length() > 0) {
			parts.add(Conf.colorSystem+desc);
		}
		//Log.debug(TextUtil.implode(parts, " "));
		return TextUtil.implode(parts, " ");
	}
	
	public static String getMaterialName(Material material) {
		String ret = material.toString();
		ret = ret.replace('_', ' ');
		ret = ret.toLowerCase();
		return ret.substring(0, 1).toUpperCase()+ret.substring(1);
	}
}


