package com.massivecraft.factions.integration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.FactionsChatListener;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.chat.EssentialsLocalChatEvent;


/*
 * This Essentials integration handler is for newer 3.x.x versions of Essentials which don't have "IEssentialsChatListener"
 * If an older version is detected in the setup() method below, handling is passed off to EssentialsOldVersionFeatures
 */

// silence deprecation warnings with this old interface
@SuppressWarnings("deprecation")
public class EssentialsFeatures
{
	private static EssentialsChat essChat;
	private static IEssentials essentials;

	public static void setup()
	{
		// integrate main essentials plugin
		// TODO: this is the old Essentials method not supported in 3.0... probably needs to eventually be moved to EssentialsOldVersionFeatures and new method implemented
		if (essentials == null)
		{
			Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
			if (ess != null && ess.isEnabled())
				essentials = (IEssentials)ess;
		}

		// integrate chat
		if (essChat != null) return;

		Plugin test = Bukkit.getServer().getPluginManager().getPlugin("EssentialsChat");
		if (test == null || !test.isEnabled()) return;

		essChat = (EssentialsChat)test;

		// try newer Essentials 3.x integration method
		try
		{
			Class.forName("com.earth2me.essentials.chat.EssentialsLocalChatEvent");
			integrateChat(essChat);
		}
		catch (ClassNotFoundException ex)
		{
			// no? try older Essentials 2.x integration method
			try
			{
				EssentialsOldVersionFeatures.integrateChat(essChat);
			}
			catch (NoClassDefFoundError ex2) { /* no known integration method, then */ }
		}
	}

	public static void unhookChat()
	{
		if (essChat == null) return;

		try
		{
			EssentialsOldVersionFeatures.unhookChat();
		}
		catch (NoClassDefFoundError ex) {}
	}


	// return false if feature is disabled or Essentials isn't available
	public static boolean handleTeleport(Player player, Location loc)
	{
		if ( ! Conf.homesTeleportCommandEssentialsIntegration || essentials == null) return false;

		Teleport teleport = (Teleport) essentials.getUser(player).getTeleport();
		Trade trade = new Trade(Conf.econCostHome, essentials);
		try
		{
			teleport.teleport(loc, trade);
		}
		catch (Exception e)
		{
			player.sendMessage(ChatColor.RED.toString()+e.getMessage());
		}
		return true;
	}


	public static void integrateChat(EssentialsChat instance)
	{
		essChat = instance;
		try
		{
			Bukkit.getServer().getPluginManager().registerEvents(new LocalChatListener(), P.p);
			P.p.log("Found and will integrate chat with newer "+essChat.getDescription().getFullName());
		}
		catch (NoSuchMethodError ex)
		{
			essChat = null;
		}
	}

	private static class LocalChatListener implements Listener
	{
		@EventHandler(priority = EventPriority.NORMAL)
		public void onPlayerChat(EssentialsLocalChatEvent event)
		{
			Player speaker = event.getPlayer();
			String format = event.getFormat();
			
			format = FactionsChatListener.parseTags(format, speaker);
			
			event.setFormat(format);
			// NOTE: above doesn't do relation coloring. if/when we can get a local recipient list from EssentialsLocalChatEvent, we'll probably
			// want to pass it on to FactionsPlayerListener.onPlayerChat(PlayerChatEvent event) rather than duplicating code
		}
	}
}
