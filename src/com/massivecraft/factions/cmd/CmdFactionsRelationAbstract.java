package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public abstract class CmdFactionsRelationAbstract extends FCommand
{
	public Rel targetRelation;
	
	public CmdFactionsRelationAbstract()
	{
		this.addAliases("faction");
		
		this.addRequirements(ReqHasPerm.get(Perm.RELATION.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		Faction them = this.arg(0, ARFaction.get());
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

		if (myFaction.getRelationWish(them) == targetRelation)
		{
			msg("<b>You already have that relation wish set with %s.", them.getTag());
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(targetRelation.getRelationCost())) return;

		// try to set the new relation
		Rel oldRelation = myFaction.getRelationTo(them, true);
		myFaction.setRelationWish(them, targetRelation);
		Rel currentRelation = myFaction.getRelationTo(them, true);

		// if the relation change was successful
		if (targetRelation == currentRelation)
		{
			// trigger the faction relation event
			FactionRelationEvent relationEvent = new FactionRelationEvent(myFaction, them, oldRelation, currentRelation);
			Bukkit.getServer().getPluginManager().callEvent(relationEvent);

			them.msg("%s<i> is now %s.", myFaction.describeTo(them, true), targetRelation.getDescFactionOne());
			myFaction.msg("%s<i> is now %s.", them.describeTo(myFaction, true), targetRelation.getDescFactionOne());
		}
		// inform the other faction of your request
		else
		{
			them.msg("%s<i> wishes to be %s.", myFaction.describeTo(them, true), targetRelation.getColor()+targetRelation.getDescFactionOne());
			them.msg("<i>Type <c>/"+ConfServer.baseCommandAliases.get(0)+" "+targetRelation+" "+myFaction.getTag()+"<i> to accept.");
			myFaction.msg("%s<i> were informed that you wish to be %s<i>.", them.describeTo(myFaction, true), targetRelation.getColor()+targetRelation.getDescFactionOne());
		}
		
		// TODO: The ally case should work!!
		//   * this might have to be bumped up to make that happen, & allow ALLY,NEUTRAL only
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

		SpoutFeatures.updateTitle(myFaction, them);
		SpoutFeatures.updateTitle(them, myFaction);
		SpoutFeatures.updateTerritoryDisplayLoc(null);
	}
}
