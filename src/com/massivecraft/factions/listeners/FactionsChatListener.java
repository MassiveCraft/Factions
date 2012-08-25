package com.massivecraft.factions.listeners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Rel;

public class FactionsChatListener implements Listener
{
	public P p;
	public FactionsChatListener(P p)
	{
		this.p = p;
	}
	
	public static Field fieldRegisteredListenerDotPriority;
	public static final Pattern parsePattern;
	static
	{
		try
		{
			fieldRegisteredListenerDotPriority = RegisteredListener.class.getDeclaredField("priority");
			fieldRegisteredListenerDotPriority.setAccessible(true);
		}
		catch (Exception e)
		{
			P.p.log(Level.SEVERE, "A reflection trick is broken! This will lead to glitchy relation-colored-chat.");
		}
		
		parsePattern = Pattern.compile("[{\\[]factions?_([a-zA-Z_]+)[}\\]]");
	}
	
	/**
	 * We offer an optional and very simple chat formating functionality.
	 */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
	public void lowPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		if (Conf.chatSetFormat)
		{
			event.setFormat(Conf.chatSetFormatTo);
		}
	}

	// this is for handling insertion of the player's faction tag, set at highest priority to give other plugins a chance to modify chat first
	
	/**
	 * At the Highest event priority we apply chat formating.
	 * Relation colored faction tags may or may not be disabled (Conf.chatParseTagsColored)
	 * If color is disabled it works flawlessly.
	 * If however color is enabled we face a limitation in Bukkit.
	 * Bukkit does not support the same message looking different for each recipient.
	 * The method we use to get around this is a bit hacky:
	 * 1. We cancel the chat event on EventPriority.HIGHEST
	 * 2. We trigger EventPriority.MONITOR manually without relation color.
	 * 3. We log in console the way it's usually done (as in nms.NetServerHandler line~793).
	 * 4. We send out the messages to each player with relation color.
	 * The side effect is that other plugins at EventPriority.HIGHEST may experience the event as cancelled. 
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		// Should we even parse?
		if ( ! Conf.chatParseTags) return;
		if (Conf.chatTagHandledByAnotherPlugin) return;
		
		Player from = event.getPlayer();
		FPlayer fpfrom = FPlayers.i.get(from);
		String format = event.getFormat();
		String message = event.getMessage();
		
		String formatWithoutColor = parseTags(format, from, fpfrom);
		
		if ( ! Conf.chatParseTagsColored)
		{
			// The case without color is really this simple (:
			event.setFormat(formatWithoutColor);
			return;
		}
		
		// So you want color eh? You monster :O
		
		// 1. We cancel the chat event on EventPriority.HIGHEST
		event.setCancelled(true);
		
		// 2. We trigger EventPriority.MONITOR manually without relation color.
		AsyncPlayerChatEvent monitorOnlyEvent = new AsyncPlayerChatEvent(false, from, message, new HashSet<Player>(Arrays.asList(Bukkit.getOnlinePlayers())));
		monitorOnlyEvent.setFormat(formatWithoutColor);
		callEventAtMonitorOnly(monitorOnlyEvent);
		
		// 3. We log in console the way it's usually done (as in nms.NetServerHandler line~793).
		Bukkit.getConsoleSender().sendMessage(String.format(monitorOnlyEvent.getFormat(), monitorOnlyEvent.getPlayer().getDisplayName(), monitorOnlyEvent.getMessage()));
		
		// 4. We send out the messages to each player with relation color.
		for (Player to : event.getRecipients())
		{
			FPlayer fpto = FPlayers.i.get(to);
			String formatWithColor = parseTags(format, from, fpfrom, to, fpto);
			to.sendMessage(String.format(formatWithColor, from.getDisplayName(), message));
        }
	}
	
	/**
	 * This is some nasty woodo - I know :/
	 * I should make a pull request to Bukkit and CraftBukkit to support this feature natively
	 */
	public static void callEventAtMonitorOnly(Event event)
	{
		synchronized(Bukkit.getPluginManager())
		{
			HandlerList handlers = event.getHandlers();
	        RegisteredListener[] listeners = handlers.getRegisteredListeners();
	        
	        for (RegisteredListener registration : listeners)
	        {
	        	try
				{
	        		EventPriority priority = (EventPriority) fieldRegisteredListenerDotPriority.get(registration);
	        		if (priority != EventPriority.MONITOR) continue;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
	        	
	        	// This rest is almost copy pasted from SimplePluginManager in Bukkit:
	        	
	        	if (!registration.getPlugin().isEnabled()) {
	                continue;
	            }
	        	
	            try {
	                registration.callEvent(event);
	            } catch (AuthorNagException ex) {
	                Plugin plugin = registration.getPlugin();

	                if (plugin.isNaggable()) {
	                    plugin.setNaggable(false);

	                    String author = "<NoAuthorGiven>";

	                    if (plugin.getDescription().getAuthors().size() > 0) {
	                        author = plugin.getDescription().getAuthors().get(0);
	                    }
	                    Bukkit.getServer().getLogger().log(Level.SEVERE, String.format(
	                            "Nag author: '%s' of '%s' about the following: %s",
	                            author,
	                            plugin.getDescription().getName(),
	                            ex.getMessage()
	                            ));
	                }
	            } catch (Throwable ex) {
	            	Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getName(), ex);
	            }
	        }
		}
	}
	
	public static String parseTags(String str, Player from)
	{
		FPlayer fpfrom = FPlayers.i.get(from);
		return parseTags(str, from, fpfrom, null, null);
	}
	public static String parseTags(String str, Player from, FPlayer fpfrom)
	{
		return parseTags(str, from, fpfrom, null, null);
	}
	public static String parseTags(String str, Player from, Player to)
	{
		FPlayer fpfrom = FPlayers.i.get(from);
		FPlayer fpto = FPlayers.i.get(to);
		return parseTags(str, from, fpfrom, to, fpto);
	}
	public static String parseTags(String str, Player from, FPlayer fpfrom, Player to, FPlayer fpto)
	{
		StringBuffer ret = new StringBuffer();
		
		Matcher matcher = parsePattern.matcher(str);
		while (matcher.find())
		{
			String[] parts = matcher.group(1).toLowerCase().split("_");
			List<String> args = new ArrayList<String>(Arrays.asList(parts));
			String tag = args.remove(0);
			matcher.appendReplacement(ret, produceTag(tag, args, from, fpfrom, to, fpto));
		}
		matcher.appendTail(ret);
		
		return ret.toString();
	}
	public static String produceTag(String tag, List<String> args, Player from, FPlayer fpfrom, Player to, FPlayer fpto)
	{
		String ret = "";
		if (tag.equals("relcolor"))
		{
			if (fpto == null)
			{
				ret = Rel.NEUTRAL.getColor().toString();
			}
			else
			{
				ret = fpfrom.getRelationTo(fpto).getColor().toString();
			}
		}
		else if (tag.startsWith("roleprefix"))
		{
			ret = fpfrom.getRole().getPrefix();
		}
		else if (tag.equals("title"))
		{
			ret = fpfrom.getTitle();
		}
		else if (tag.equals("tag"))
		{
			if (fpfrom.hasFaction())
			{
				ret = fpfrom.getFaction().getTag();
			}
		}
		else if (tag.startsWith("tagforce"))
		{
			ret = fpfrom.getFaction().getTag();
		}
		
		if (ret == null) ret = "";
		
		return applyFormatsByName(ret, args);
	}
	public static String applyFormatsByName(String str, List<String> formatNames)
	{
		if (str.length() == 0) return str;
		for (String formatName : formatNames)
		{
			String format = Conf.chatSingleFormats.get(formatName);
			try
			{
				str = String.format(format, str);
			}
			catch (Exception e) { }
		}
		return str;
	}
	
}
