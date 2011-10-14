package com.massivecraft.factions.integration.capi;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.massivecraft.capi.Channel;
import com.massivecraft.capi.Channels;
import com.massivecraft.capi.events.CAPIListChannelsEvent;
import com.massivecraft.capi.events.CAPIMessageToChannelEvent;
import com.massivecraft.capi.events.CAPIMessageToPlayerEvent;
import com.massivecraft.capi.events.CAPISelectChannelEvent;
import com.massivecraft.capi.listeners.CapiListener;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;

public class PluginCapiListener extends CapiListener
{
	P p;
	
	Set<String> myChannelIds = new LinkedHashSet<String>();
	
	public PluginCapiListener(P p)
	{
		this.p = p;
		
		myChannelIds.add("faction");
		myChannelIds.add("allies");
	}
	
	private String replacePlayerTags(String format, FPlayer me, FPlayer you)
	{
		String meFactionTag = me.getChatTag(you);
		format = format.replace("{ME_FACTIONTAG}",      meFactionTag.length() == 0 ? "" : meFactionTag);
		format = format.replace("{ME_FACTIONTAG_PADR}", meFactionTag.length() == 0 ? "" : meFactionTag+" ");
		format = format.replace("{ME_FACTIONTAG_PADL}", meFactionTag.length() == 0 ? "" : " "+meFactionTag);
		format = format.replace("{ME_FACTIONTAG_PADB}", meFactionTag.length() == 0 ? "" : " "+meFactionTag+" ");

		String youFactionTag = you.getChatTag(me);
		format = format.replace("{YOU_FACTIONTAG}",      youFactionTag.length() == 0 ? "" : youFactionTag);
		format = format.replace("{YOU_FACTIONTAG_PADR}", youFactionTag.length() == 0 ? "" : youFactionTag+" ");
		format = format.replace("{YOU_FACTIONTAG_PADL}", youFactionTag.length() == 0 ? "" : " "+youFactionTag);
		format = format.replace("{YOU_FACTIONTAG_PADB}", youFactionTag.length() == 0 ? "" : " "+youFactionTag+" ");
		
		return format;
	}
	
	@Override
	public void onListChannelsEvent(CAPIListChannelsEvent event)
	{
		for (Channel c : Channels.i.get())
		{
			if (myChannelIds.contains(c.getId()))
			{
				event.getChannels().add(c);
			}
		}
	}
	
	@Override
	public void onMessageToChannel(CAPIMessageToChannelEvent event)
	{
		if (event.isCancelled()) return;
		if ( ! myChannelIds.contains(event.getChannel().getId())) return;
		
		Player me = event.getMe();
		FPlayer fme = FPlayers.i.get(me);
		Faction myFaction = fme.getFaction();
		
		if (event.getChannel().getId().equals("faction") && myFaction.isNormal())
		{
			event.getThem().addAll(myFaction.getOnlinePlayers());
		}
		else if (event.getChannel().getId().equals("allies"))
		{
			for (Player somePlayer : Bukkit.getServer().getOnlinePlayers())
			{
				FPlayer someFPlayer = FPlayers.i.get(somePlayer);
				if (someFPlayer.getRelationTo(fme).value >= Relation.ALLY.value)
				{
					event.getThem().add(somePlayer);
				}
			}
		}
	}
	
	@Override
	public void onMessageToPlayer(CAPIMessageToPlayerEvent event)
	{
		if (event.isCancelled()) return;
		event.setFormat(this.replacePlayerTags(event.getFormat(), FPlayers.i.get(event.getMe()), FPlayers.i.get(event.getYou())));
	}
	
	@Override
	public void onSelectChannel(CAPISelectChannelEvent event)
	{
		if (event.isCancelled()) return;
		String channelId = event.getChannel().getId();
		if ( ! myChannelIds.contains(channelId)) return;
		
		Player me = event.getMe();
		FPlayer fme = FPlayers.i.get(me);
		
		if ( ! fme.hasFaction())
		{
			event.setFailMessage(p.txt.parse("<b>You must be member in a faction to use this channel."));
			event.setCancelled(true);
		}
	}
}
