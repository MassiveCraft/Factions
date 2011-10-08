package com.massivecraft.factions.zcore.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class TextUtil
{
	private Map<String, String> tags = new HashMap<String, String>();
	private Map<String, String> lang = new HashMap<String, String>();
	
	public TextUtil(Map<String, String> tags, Map<String, String> lang)
	{
		if (tags != null)
		{
			this.tags.putAll(tags);
		}
		
		if (lang != null)
		{
			this.lang.putAll(lang);
		}
	}
	
	// Get is supposed to be the way we reach registered lang
	// TODO: Is the parse
	public String get(String name)
	{
		String str = lang.get(name);
		if (str == null) str = name;
		
		return this.parse(str);
	}
	
	public String get(String name, Object... args)
	{
		String str = lang.get(name);
		if (str == null) str = name;
		
		return this.parse(str, args);
	}
	
	// Parse is used to handle non registered text
	public String parse(String str, Object... args)
	{
		return String.format(this.tags(str), args);
	}
	
	public String parse(String str)
	{
		return this.tags(str);
	}
	
	public Map<String, String> getTags()
	{
		return tags;
	}

	public Map<String, String> getLang()
	{
		return lang;
	}
	
	public String tags(String str)
	{
		return replaceTags(str, this.tags);
	}
	
	public static final transient Pattern patternTag = Pattern.compile("<([^<>]*)>");
	public static String replaceTags(String str, Map<String, String> tags)
	{
		StringBuffer ret = new StringBuffer();
		Matcher matcher = patternTag.matcher(str);
		while (matcher.find())
		{
			String tag = matcher.group(1);
			String repl = tags.get(tag);
			if (repl == null)
			{
				matcher.appendReplacement(ret, "<"+tag+">");
			}
			else
			{
				matcher.appendReplacement(ret, repl);
			}
		}
		matcher.appendTail(ret);
		return ret.toString();
	}
	
	public static String implode(List<String> list, String glue)
	{
	    StringBuilder ret = new StringBuilder();
	    for (int i=0; i<list.size(); i++)
	    {
	        if (i!=0)
	        {
	        	ret.append(glue);
	        }
	        ret.append(list.get(i));
	    }
	    return ret.toString();
	}
	
	public static String repeat(String s, int times)
	{
	    if (times <= 0) return "";
	    else return s + repeat(s, times-1);
	}
	
	public static String getMaterialName(Material material)
	{
		return material.toString().replace('_', ' ').toLowerCase();
	}
	
	public static String getMaterialName(int materialId)
	{
		return getMaterialName(Material.getMaterial(materialId));
	}
	
	public static String upperCaseFirst(String string)
	{
		return string.substring(0, 1).toUpperCase()+string.substring(1);
	}
	
	// TODO: Make part of layout configuration.
	private final static String titleizeLine = repeat("_", 52);
	private final static int titleizeBalance = -1;
	public String titleize(String str)
	{
		String center = ".[ "+ tags("<l>") + str + tags("<a>")+ " ].";
		int centerlen = ChatColor.stripColor(center).length();
		int pivot = titleizeLine.length() / 2;
		int eatLeft = (centerlen / 2) - titleizeBalance;
		int eatRight = (centerlen - eatLeft) + titleizeBalance;

		if (eatLeft < pivot)
			return tags("<a>")+titleizeLine.substring(0, pivot - eatLeft) + center + titleizeLine.substring(pivot + eatRight);
		else
			return tags("<a>")+center;
	}
	
	public ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title)
	{
		ArrayList<String> ret = new ArrayList<String>();
		int pageZeroBased = pageHumanBased - 1;
		int pageheight = 9;
		int pagecount = (lines.size() / pageheight)+1;
		
		ret.add(this.titleize(title+" "+pageHumanBased+"/"+pagecount));
		
		if (pagecount == 0)
		{
			ret.add(this.tags("<i>Sorry. No Pages available."));
			return ret;
		}
		else if (pageZeroBased < 0 || pageHumanBased > pagecount)
		{
			ret.add(this.tags("<i>Invalid page. Must be between 1 and "+pagecount));
			return ret;
		}
		
		int from = pageZeroBased * pageheight;
		int to = from+pageheight;
		if (to > lines.size())
		{
			to = lines.size();
		}
		
		ret.addAll(lines.subList(from, to));
		
		return ret;
	}
	
	/**
	 * Using this function you transform a delta in milliseconds
	 * to a String like "2 weeks from now" or "7 days ago".
	 */
	public static final long millisPerSecond = 1000;
	public static final long millisPerMinute =   60 * millisPerSecond;
	public static final long millisPerHour   =   60 * millisPerMinute;
	public static final long millisPerDay    =   24 * millisPerHour;
	public static final long millisPerWeek   =    7 * millisPerDay;
	public static final long millisPerMonth  =   31 * millisPerDay;
	public static final long millisPerYear   =  365 * millisPerDay;
	public static String getTimeDeltaDescriptionRelNow(long millis)
	{
		double absmillis = (double) Math.abs(millis);
		String agofromnow = "from now";
		String unit;
		long num;
		if (millis <= 0)
		{
			agofromnow = "ago";
		}
		
		// We use a factor 3 below for a reason... why do you think?
		// Answer: it is a way to make our round of error smaller.
		if (absmillis < 3 * millisPerSecond)
		{
			unit = "milliseconds";
			num = (long) (absmillis);
		}
		else if (absmillis < 3 * millisPerMinute)
		{
			unit = "seconds";
			num = (long) (absmillis / millisPerSecond);
		}
		else if (absmillis < 3 * millisPerHour)
		{
			unit = "minutes";
			num = (long) (absmillis / millisPerMinute);
		}
		else if (absmillis <  3 * millisPerDay)
		{
			unit = "hours";
			num = (long) (absmillis / millisPerHour);
		}
		else if (absmillis < 3 * millisPerWeek)
		{
			unit = "days";
			num = (long) (absmillis / millisPerDay);
		}
		else if (absmillis < 3 * millisPerMonth)
		{
			unit = "weeks";
			num = (long) (absmillis / millisPerWeek);
		}
		else if (absmillis < 3 * millisPerYear)
		{
			unit = "months";
			num = (long) (absmillis / millisPerMonth);
		}
		else
		{
			unit = "years";
			num = (long) (absmillis / millisPerYear);
		}
		
		return ""+num+" "+unit+" "+agofromnow;
	}
}
