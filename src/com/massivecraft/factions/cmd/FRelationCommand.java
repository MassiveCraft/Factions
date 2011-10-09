package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;

public abstract class FRelationCommand extends FCommand
{
	public Relation targetRelation;
	
	public FRelationCommand()
	{
		super();
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("player name", "you");
		
		this.permission = Permission.RELATION.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		Faction them = this.argAsFaction(0);
		
		if ( ! them.isNormal())
		{
			sendMessageParsed("<b>Nope! You can't.");
			return;
		}
		
		if (them == myFaction)
		{
			sendMessageParsed("<b>Nope! You can't declare a relation to yourself :)");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(targetRelation.getRelationCost())) return;

		myFaction.setRelationWish(them, targetRelation);
		Relation currentRelation = myFaction.getRelation(them, true);
		ChatColor currentRelationColor = currentRelation.getColor();
		if (targetRelation.value == currentRelation.value)
		{
			them.sendMessageParsed("<i>Your faction is now "+currentRelationColor+targetRelation.toString()+"<i> to "+currentRelationColor+myFaction.getTag());
			myFaction.sendMessageParsed("<i>Your faction is now "+currentRelationColor+targetRelation.toString()+"<i> to "+currentRelationColor+them.getTag());
		}
		else
		{
			them.sendMessageParsed(currentRelationColor+myFaction.getTag()+"<i> wishes to be your "+targetRelation.getColor()+targetRelation.toString());
			them.sendMessageParsed("<i>Type <c>/"+Conf.baseCommandAliases.get(0)+" "+targetRelation+" "+myFaction.getTag()+"<i> to accept.");
			myFaction.sendMessageParsed(currentRelationColor+them.getTag()+"<i> were informed that you wish to be "+targetRelation.getColor()+targetRelation);
		}
		
		if ( ! targetRelation.isNeutral() && them.isPeaceful())
		{
			them.sendMessageParsed("<i>This will have no effect while your faction is peaceful.");
			myFaction.sendMessageParsed("<i>This will have no effect while their faction is peaceful.");
		}
		
		if ( ! targetRelation.isNeutral() && myFaction.isPeaceful())
		{
			them.sendMessageParsed("<i>This will have no effect while their faction is peaceful.");
			myFaction.sendMessageParsed("<i>This will have no effect while your faction is peaceful.");
		}

		SpoutFeatures.updateAppearances(myFaction, them);
		
	}
}
