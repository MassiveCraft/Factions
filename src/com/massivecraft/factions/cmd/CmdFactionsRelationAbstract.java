package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.FactionsEventRelationChange;
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
		// Args
		Faction otherFaction = this.arg(0, ARFaction.get(sender));
		if (otherFaction == null) return;
		
		Rel newRelation = targetRelation;
		
		/*if ( ! them.isNormal())
		{
			msg("<b>Nope! You can't.");
			return;
		}*/
		
		// Verify
		
		if (otherFaction == myFaction)
		{
			msg("<b>Nope! You can't declare a relation to yourself :)");
			return;
		}

		if (myFaction.getRelationWish(otherFaction) == newRelation)
		{
			msg("<b>You already have that relation wish set with %s.", otherFaction.getName());
			return;
		}
		
		// Event
		FactionsEventRelationChange event = new FactionsEventRelationChange(sender, myFaction, otherFaction, newRelation);
		event.run();
		if (event.isCancelled()) return;
		newRelation = event.getNewRelation();

		// try to set the new relation
		myFaction.setRelationWish(otherFaction, newRelation);
		Rel currentRelation = myFaction.getRelationTo(otherFaction, true);

		// if the relation change was successful
		if (newRelation == currentRelation)
		{
			otherFaction.msg("%s<i> is now %s.", myFaction.describeTo(otherFaction, true), newRelation.getDescFactionOne());
			myFaction.msg("%s<i> is now %s.", otherFaction.describeTo(myFaction, true), newRelation.getDescFactionOne());
		}
		// inform the other faction of your request
		else
		{
			otherFaction.msg("%s<i> wishes to be %s.", myFaction.describeTo(otherFaction, true), newRelation.getColor()+newRelation.getDescFactionOne());
			otherFaction.msg("<i>Type <c>/"+ConfServer.baseCommandAliases.get(0)+" "+newRelation+" "+myFaction.getName()+"<i> to accept.");
			myFaction.msg("%s<i> were informed that you wish to be %s<i>.", otherFaction.describeTo(myFaction, true), newRelation.getColor()+newRelation.getDescFactionOne());
		}
		
		// TODO: The ally case should work!!
		//   * this might have to be bumped up to make that happen, & allow ALLY,NEUTRAL only
		if ( newRelation != Rel.TRUCE && otherFaction.getFlag(FFlag.PEACEFUL))
		{
			otherFaction.msg("<i>This will have no effect while your faction is peaceful.");
			myFaction.msg("<i>This will have no effect while their faction is peaceful.");
		}
		
		if ( newRelation != Rel.TRUCE && myFaction.getFlag(FFlag.PEACEFUL))
		{
			otherFaction.msg("<i>This will have no effect while their faction is peaceful.");
			myFaction.msg("<i>This will have no effect while your faction is peaceful.");
		}
	}
}
