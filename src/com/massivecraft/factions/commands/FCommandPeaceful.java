package com.massivecraft.factions.commands;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;

public class FCommandPeaceful extends FCommand
{
	
	public FCommandPeaceful()
	{
		super();
		this.aliases.add("peaceful");
		
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_SET_PEACEFUL.node;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		Faction faction = this.argAsFaction(0);
		if (faction == null) return;

		String change;
		if (faction.isPeaceful())
		{
			change = "removed peaceful status from";
			faction.setPeaceful(false);
		}
		else
		{
			change = "granted peaceful status to";
			faction.setPeaceful(true);
		}
		
		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			if (fplayer.getFaction() == faction)
			{
				fplayer.sendMessageParsed(fme.getNameAndRelevant(fplayer)+"<i> has "+change+" your faction.");
			}
			else
			{
				fplayer.sendMessageParsed(fme.getNameAndRelevant(fplayer)+"<i> has "+change+" the faction \"" + faction.getTag(fplayer) + "\".");
			}
		}

		SpoutFeatures.updateAppearances(faction);
	}
	
}
