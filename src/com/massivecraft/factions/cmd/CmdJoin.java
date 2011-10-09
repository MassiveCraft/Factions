package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdJoin extends FCommand
{
	public CmdJoin()
	{
		super();
		this.aliases.add("join");
		
		this.requiredArgs.add("faction name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.JOIN.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		Faction faction = this.argAsFaction(0);
		if (faction == null) return;

		if ( ! faction.isNormal())
		{
			sendMessageParsed("<b>You may only join normal factions. This is a system faction.");
			return;
		}
		
		if (faction == myFaction)
		{
			sendMessageParsed("<b>You are already a member of %s", faction.getTag(fme));
			return;
		}
		
		if (fme.hasFaction())
		{
			sendMessageParsed("<b>You must leave your current faction first.");
			return;
		}
		
		if (!Conf.CanLeaveWithNegativePower && fme.getPower() < 0)
		{
			sendMessageParsed("<b>You cannot join a faction until your power is positive.");
			return;
		}
		
		if( ! faction.getOpen() && ! faction.isInvited(fme))
		{
			sendMessageParsed("<i>This guild requires invitation.");
			faction.sendMessageParsed("%s<i> tried to join your faction.", fme.getNameAndRelevant(faction));
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostJoin))
		{
			return;
		}

		fme.sendMessageParsed("<i>You successfully joined %s", faction.getTag(fme));
		faction.sendMessageParsed("<i>%s joined your faction.", fme.getNameAndRelevant(faction));
		
		fme.resetFactionData();
		fme.setFaction(faction);
		faction.deinvite(fme);
	}
}
