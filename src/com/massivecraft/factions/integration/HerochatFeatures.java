package com.massivecraft.factions.integration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Rel;

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
		if (plug != null && plug.getClass().getName().equals("com.dthielke.herochat.Herochat"))
		{
			P.p.log("Integration with Herochat successful");
			Bukkit.getPluginManager().registerEvents(new HerochatFeatures(P.p), P.p);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onChannelChatEvent(ChannelChatEvent event)
	{
		Channel ch = event.getChannel();
		boolean isFactionChat = ch.getName().equals(Conf.herochatFactionChannelName);
		boolean isAllyChat = ch.getName().equals(Conf.herochatAllyChannelName);
		if ( ! isFactionChat && ! isAllyChat) return;
		
		// Do common setup
		Player sender = event.getSender().getPlayer();
		FPlayer fpsender = FPlayers.i.get(sender);
		event.getBukkitEvent().getRecipients().clear();
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
}
