package com.massivecraft.factions.commands;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;


public class FCommandPermanent extends FCommand
{
	public FCommandPermanent()
	{
		super();
		this.aliases.add("permanent");
		
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_SET_PERMANENT.node;
		
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
		if(faction.isPermanent())
		{
			change = "removed permanent status from";
			faction.setPermanent(false);
		}
		else
		{
			change = "added permanent status to";
			faction.setPermanent(true);
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
	}
}
