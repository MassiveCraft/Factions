package com.massivecraft.factions.integration.herochat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Herochat;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.FactionsChatListener;

public class HerochatFeatures implements Listener
{
	P p;
	public HerochatFeatures(P p)
	{
		this.p = p;
	}
	
	public static void setup()
	{
		Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("Herochat");
		if (plug == null) return;
		if (!plug.getClass().getName().equals("com.dthielke.herochat.Herochat")) return;
		Bukkit.getPluginManager().registerEvents(new HerochatFeatures(P.p), P.p);
		Herochat.getChannelManager().addChannel(new FactionChannel());
		Herochat.getChannelManager().addChannel(new AlliesChannel());
		P.p.log("Integration with Herochat successful");
	}
	
	/**
	 * Due to limitations in the new version of Herochat we can not offer relation colored tags.
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChannelChatEvent(ChannelChatEvent event)
	{
		// Should we even parse?
		if ( ! Conf.chatParseTags) return;
		if (Conf.chatTagHandledByAnotherPlugin) return;
		
		Player from = event.getSender().getPlayer();
		FPlayer fpfrom = FPlayers.i.get(from);
		String format = event.getFormat();
		
		format = format.replaceAll("&r", "§r");
		
		String formatWithoutColor = FactionsChatListener.parseTags(format, from, fpfrom);
				
		event.setFormat(formatWithoutColor);
	}
	
	/*
	
	public static Chatter getChatter(Player player)
	{
		ChatterManager chatterManager = Herochat.getChatterManager();
		if (!chatterManager.hasChatter(player)) chatterManager.addChatter(player);
		Chatter ret = chatterManager.getChatter(player);
		if (ret == null) throw new RuntimeException("Chatter (" + player.getName() + ") not found.");
		return ret;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		Chatter sender = getChatter(player);
		Channel ch = sender.getActiveChannel();
		if (ch == null) return;
		
		boolean isFactionChat = ch.getName().equals(Conf.herochatFactionChannelName);
		boolean isAllyChat = ch.getName().equals(Conf.herochatAllyChannelName);
		if ( ! isFactionChat && ! isAllyChat) return;
		
		// Do common setup
		Player sender = event.getSender().getPlayer();
		FPlayer fpsender = FPlayers.i.get(sender);
		
		event.getRecipients().clear();
		
		if ( ! fpsender.hasFaction())
		{
			sender.sendMessage(ChatColor.YELLOW.toString()+"You must join a faction to use the "+ch.getColor().toString()+ch.getName()+ChatColor.YELLOW.toString()+"-channel.");
			event.getBukkitEvent().setCancelled(true);
			return;
		}
		
		Faction faction = fpsender.getFaction();
		event.getBukkitEvent().getRecipients().addAll(faction.getOnlinePlayers());
		
		if (isAllyChat)
		{
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				if(faction.getRelationTo(fplayer) == Rel.ALLY)
				{
					event.getBukkitEvent().getRecipients().add(fplayer.getPlayer());
				}
			}
		}
	}
	
	*/
}
