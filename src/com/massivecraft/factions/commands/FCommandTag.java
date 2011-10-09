package com.massivecraft.factions.commands;

import java.util.ArrayList;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;

public class FCommandTag extends FCommand
{
	
	public FCommandTag()
	{
		this.aliases.add("tag");
		
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_TAG.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}
		
		String tag = this.argAsString(0);
		
		// TODO does not first test cover selfcase?
		if (Factions.i.isTagTaken(tag) && ! MiscUtil.getComparisonString(tag).equals(myFaction.getComparisonTag()))
		{
			sendMessageParsed("<b>That tag is already taken");
			return;
		}
		
		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(Factions.validateTag(tag));
		if (errors.size() > 0)
		{
			sendMessage(errors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostTag)) return;
		
		String oldtag = myFaction.getTag();
		myFaction.setTag(tag);
		
		// Inform
		myFaction.sendMessageParsed("%s<i> changed your faction tag to %s", fme.getNameAndRelevant(myFaction), myFaction.getTag(myFaction));
		for (Faction faction : Factions.i.get())
		{
			if (faction == myFaction)
			{
				continue;
			}
			faction.sendMessageParsed("<i>The faction %s<i> changed their name to %s.", fme.getRelationColor(faction)+oldtag, myFaction.getTag(faction));
		}

		if (Conf.spoutFactionTagsOverNames)
		{
			SpoutFeatures.updateAppearances(myFaction);
		}
	}
	
}
