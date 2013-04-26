package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.FactionsEventRelationChange;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public abstract class CmdFactionsRelationAbstract extends FCommand
{
	public Rel targetRelation;
	
	public CmdFactionsRelationAbstract()
	{
		this.addRequiredArg("faction");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.RELATION.node));
		this.addRequirements(ReqHasFaction.get());
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
		
		if (otherFaction == usenderFaction)
		{
			msg("<b>Nope! You can't declare a relation to yourself :)");
			return;
		}

		if (usenderFaction.getRelationWish(otherFaction) == newRelation)
		{
			msg("<b>You already have that relation wish set with %s.", otherFaction.getName());
			return;
		}
		
		// Event
		FactionsEventRelationChange event = new FactionsEventRelationChange(sender, usenderFaction, otherFaction, newRelation);
		event.run();
		if (event.isCancelled()) return;
		newRelation = event.getNewRelation();

		// try to set the new relation
		usenderFaction.setRelationWish(otherFaction, newRelation);
		Rel currentRelation = usenderFaction.getRelationTo(otherFaction, true);

		// if the relation change was successful
		if (newRelation == currentRelation)
		{
			otherFaction.msg("%s<i> is now %s.", usenderFaction.describeTo(otherFaction, true), newRelation.getDescFactionOne());
			usenderFaction.msg("%s<i> is now %s.", otherFaction.describeTo(usenderFaction, true), newRelation.getDescFactionOne());
		}
		// inform the other faction of your request
		else
		{
			otherFaction.msg("%s<i> wishes to be %s.", usenderFaction.describeTo(otherFaction, true), newRelation.getColor()+newRelation.getDescFactionOne());
			otherFaction.msg("<i>Type <c>/"+ConfServer.baseCommandAliases.get(0)+" "+newRelation+" "+usenderFaction.getName()+"<i> to accept.");
			usenderFaction.msg("%s<i> were informed that you wish to be %s<i>.", otherFaction.describeTo(usenderFaction, true), newRelation.getColor()+newRelation.getDescFactionOne());
		}
		
		// TODO: The ally case should work!!
		//   * this might have to be bumped up to make that happen, & allow ALLY,NEUTRAL only
		if ( newRelation != Rel.TRUCE && otherFaction.getFlag(FFlag.PEACEFUL))
		{
			otherFaction.msg("<i>This will have no effect while your faction is peaceful.");
			usenderFaction.msg("<i>This will have no effect while their faction is peaceful.");
		}
		
		if ( newRelation != Rel.TRUCE && usenderFaction.getFlag(FFlag.PEACEFUL))
		{
			otherFaction.msg("<i>This will have no effect while their faction is peaceful.");
			usenderFaction.msg("<i>This will have no effect while your faction is peaceful.");
		}
	}
}
