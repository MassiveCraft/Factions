package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public abstract class FRelationCommand extends FCommand
{
	public Rel targetRelation;
	
	public FRelationCommand()
	{
		super();
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("player name", "you");
		
		this.permission = Permission.RELATION.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		Faction them = this.argAsFaction(0);
		if (them == null) return;
		
		/*if ( ! them.isNormal())
		{
			msg("<b>Nope! You can't.");
			return;
		}*/
		
		if (them == myFaction)
		{
			msg("<b>Nope! You can't declare a relation to yourself :)");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(targetRelation.getRelationCost(), "to change a relation wish", "for changing a relation wish")) return;

		myFaction.setRelationWish(them, targetRelation);
		Rel currentRelation = myFaction.getRelationTo(them, true);
		if (targetRelation == currentRelation)
		{
			them.msg("%s<i> is now %s.", myFaction.describeTo(them, true), targetRelation.getDescFactionOne());
			myFaction.msg("%s<i> is now %s.", them.describeTo(myFaction, true), targetRelation.getDescFactionOne());
		}
		else
		{
			them.msg("%s<i> wishes to be %s.", myFaction.describeTo(them, true), targetRelation.getColor()+targetRelation.getDescFactionOne());
			them.msg("<i>Type <c>/"+Conf.baseCommandAliases.get(0)+" "+targetRelation+" "+myFaction.getTag()+"<i> to accept.");
			myFaction.msg("%s<i> were informed that you wish to be %s<i>.", them.describeTo(myFaction, true), targetRelation.getColor()+targetRelation.getDescFactionOne());
		}
		
		// TODO: The ally case should work!!
		if ( targetRelation != Rel.TRUCE && them.getFlag(FFlag.PEACEFUL))
		{
			them.msg("<i>This will have no effect while your faction is peaceful.");
			myFaction.msg("<i>This will have no effect while their faction is peaceful.");
		}
		
		if ( targetRelation != Rel.TRUCE && myFaction.getFlag(FFlag.PEACEFUL))
		{
			them.msg("<i>This will have no effect while their faction is peaceful.");
			myFaction.msg("<i>This will have no effect while your faction is peaceful.");
		}

		SpoutFeatures.updateAppearances(myFaction, them);
		SpoutFeatures.updateTerritoryDisplayLoc(null);
	}
}
