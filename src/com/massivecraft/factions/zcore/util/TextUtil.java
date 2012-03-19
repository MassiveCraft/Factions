package com.massivecraft.factions.zcore.util;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class TextUtil
{
	public Map<String, String> tags;
	public TextUtil()
	{
		this.tags = new HashMap<String, String>();
	}
	
	// -------------------------------------------- //
	// Top-level parsing functions.
	// -------------------------------------------- //
	
	public String parse(String str, Object... args)
	{
		return String.format(this.parse(str), args);
	}
	
	public String parse(String str)
	{
		return this.parseTags(parseColor(str));
	}
	
	// -------------------------------------------- //
	// Tag parsing
	// -------------------------------------------- //
	
	public String parseTags(String str)
	{
		return replaceTags(str, this.tags);
	}
	
	public static final transient Pattern patternTag = Pattern.compile("<([a-zA-Z0-9_]*)>");
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
	
	// -------------------------------------------- //
	// Color parsing
	// -------------------------------------------- //
	
	public static String parseColor(String string)
	{
		string = parseColorAmp(string);
		string = parseColorAcc(string);
		string = parseColorTags(string);
		return string;
	}
	
	public static String parseColorAmp(String string)
	{
		string = string.replaceAll("(§([a-z0-9]))", "\u00A7$2");
		string = string.replaceAll("(&([a-z0-9]))", "\u00A7$2");
		string = string.replace("&&", "&");
		return string;
	}
	
	public static String parseColorAcc(String string)
	{
		return string.replace("`e", "")
		.replace("`r", ChatColor.RED.toString()) .replace("`R", ChatColor.DARK_RED.toString())
		.replace("`y", ChatColor.YELLOW.toString()) .replace("`Y", ChatColor.GOLD.toString())
		.replace("`g", ChatColor.GREEN.toString()) .replace("`G", ChatColor.DARK_GREEN.toString())
		.replace("`a", ChatColor.AQUA.toString()) .replace("`A", ChatColor.DARK_AQUA.toString())
		.replace("`b", ChatColor.BLUE.toString()) .replace("`B", ChatColor.DARK_BLUE.toString())
		.replace("`p", ChatColor.LIGHT_PURPLE.toString()) .replace("`P", ChatColor.DARK_PURPLE.toString())
		.replace("`k", ChatColor.BLACK.toString()) .replace("`s", ChatColor.GRAY.toString())
		.replace("`S", ChatColor.DARK_GRAY.toString()) .replace("`w", ChatColor.WHITE.toString());
	}
	
	public static String parseColorTags(String string)
	{
		return string.replace("<empty>", "")
		.replace("<black>", "\u00A70")
		.replace("<navy>", "\u00A71")
		.replace("<green>", "\u00A72")
		.replace("<teal>", "\u00A73")
		.replace("<red>", "\u00A74")
		.replace("<purple>", "\u00A75")
		.replace("<gold>", "\u00A76")
		.replace("<silver>", "\u00A77")
		.replace("<gray>", "\u00A78")
		.replace("<blue>", "\u00A79")
		.replace("<lime>", "\u00A7a")
		.replace("<aqua>", "\u00A7b")
		.replace("<rose>", "\u00A7c")
		.replace("<pink>", "\u00A7d")
		.replace("<yellow>", "\u00A7e")
		.replace("<white>", "\u00A7f");
	}
	
	// -------------------------------------------- //
	// Standard utils like UCFirst, implode and repeat.
	// -------------------------------------------- //
	
	public static String upperCaseFirst(String string)
	{
		return string.substring(0, 1).toUpperCase()+string.substring(1);
	}
	
	public static String repeat(String s, int times)
	{
		if (times <= 0) return "";
		else return s + repeat(s, times-1);
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
	
	public static String implodeCommaAnd(List<String> list, String comma, String and)
	{
		if (list.size() == 0) return "";
		if (list.size() == 1) return list.get(0);
		
		String lastItem = list.get(list.size()-1);
		String nextToLastItem = list.get(list.size()-2);
		String merge = nextToLastItem+and+lastItem;
		list.set(list.size()-2, merge);
		list.remove(list.size()-1);
		
		return implode(list, comma);
	}
	public static String implodeCommaAnd(List<String> list)
	{
		return implodeCommaAnd(list, ", ", " and ");
	}
	
	// -------------------------------------------- //
	// Material name tools
	// -------------------------------------------- //
	
	public static String getMaterialName(Material material)
	{
		return material.toString().replace('_', ' ').toLowerCase();
	}
	
	public static String getMaterialName(int materialId)
	{
		return getMaterialName(Material.getMaterial(materialId));
	}
	
	// -------------------------------------------- //
	// Paging and chrome-tools like titleize
	// -------------------------------------------- //
	
	private final static String titleizeLine = repeat("_", 52);
	private final static int titleizeBalance = -1;
	public String titleize(String str)
	{
		String center = ".[ "+ parseTags("<l>") + str + parseTags("<a>")+ " ].";
		int centerlen = ChatColor.stripColor(center).length();
		int pivot = titleizeLine.length() / 2;
		int eatLeft = (centerlen / 2) - titleizeBalance;
		int eatRight = (centerlen - eatLeft) + titleizeBalance;

		if (eatLeft < pivot)
			return parseTags("<a>")+titleizeLine.substring(0, pivot - eatLeft) + center + titleizeLine.substring(pivot + eatRight);
		else
			return parseTags("<a>")+center;
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
			ret.add(this.parseTags("<i>Sorry. No Pages available."));
			return ret;
		}
		else if (pageZeroBased < 0 || pageHumanBased > pagecount)
		{
			ret.add(this.parseTags("<i>Invalid page. Must be between 1 and "+pagecount));
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
	
	// -------------------------------------------- //
	// Describing Time
	// -------------------------------------------- //
	
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
	
	public static Map<String, Long> unitMillis;
	
	static
	{
		unitMillis = new LinkedHashMap<String, Long>();
		unitMillis.put("years", millisPerYear);
		unitMillis.put("months", millisPerMonth);
		unitMillis.put("weeks", millisPerWeek);
		unitMillis.put("days", millisPerDay);
		unitMillis.put("hours", millisPerHour);
		unitMillis.put("minutes", millisPerMinute);
		unitMillis.put("seconds", millisPerSecond);
	}
	
	public static String getTimeDeltaDescriptionRelNow(long millis)
	{
		String ret = "";
		
		double millisLeft = (double) Math.abs(millis);
		
		List<String> unitCountParts = new ArrayList<String>();
		for (Entry<String, Long> entry : unitMillis.entrySet())
		{
			if (unitCountParts.size() == 3 ) break;
			String unitName = entry.getKey();
			long unitSize = entry.getValue();
			long unitCount = (long) Math.floor(millisLeft / unitSize);
			if (unitCount < 1) continue;
			millisLeft -= unitSize*unitCount;
			unitCountParts.add(unitCount+" "+unitName);
		}
		
		if (unitCountParts.size() == 0) return "just now";
		
		ret += implodeCommaAnd(unitCountParts);
		ret += " ";
		if (millis <= 0)
		{
			ret += "ago";
		}
		else
		{
			ret += "from now";
		}
		
		return ret;
	}
	
	// -------------------------------------------- //
	// String comparison
	// -------------------------------------------- //
	
	public static String getBestStartWithCI(Collection<String> candidates, String start)
	{
		String ret = null;
		int best = 0;
		
		start = start.toLowerCase();
		int minlength = start.length();
		for (String candidate : candidates)
		{
			if (candidate.length() < minlength) continue;
			if ( ! candidate.toLowerCase().startsWith(start)) continue;
			
			// The closer to zero the better
			int lendiff = candidate.length() - minlength;
			if (lendiff == 0)
			{
				return candidate;
			}
			if (lendiff < best || best == 0)
			{
				best = lendiff;
				ret = candidate;
			}
		}
		return ret;
	}
}
