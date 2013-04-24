package com.massivecraft.factions.util;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.ChatColor;

public class MiscUtil
{	
	// Inclusive range
	public static long[] range(long start, long end) {
		long[] values = new long[(int) Math.abs(end - start) + 1];
		
		if (end < start) {
			long oldstart = start;
			start = end;
			end = oldstart;
		}
	
		for (long i = start; i <= end; i++) {
			values[(int) (i - start)] = i;
		}
		
		return values;
	}
	
	public static HashSet<String> substanceChars = new HashSet<String>(Arrays.asList(new String []{
	"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", 
	"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", 
	"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", 
	"s", "t", "u", "v", "w", "x", "y", "z"
	}));
			
	public static String getComparisonString(String str)
	{
		String ret = "";
		
		str = ChatColor.stripColor(str);
		str = str.toLowerCase();
		
		for (char c : str.toCharArray())
		{
			if (substanceChars.contains(String.valueOf(c)))
			{
				ret += c;
			}
		}
		return ret.toLowerCase();
	}
	
}

