package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsRelationChange;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public abstract class CmdFactionsRelationAbstract extends FactionsCommand
{
	public Rel targetRelation;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRelationAbstract()
	{
		// Aliases
		this.addRequiredArg("faction");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.RELATION.node));
		this.addRequirements(ReqHasFaction.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Faction otherFaction = this.arg(0, ARFaction.get());
		if (otherFaction == null) return;
		
		Rel newRelation = targetRelation;
		
		/*if ( ! them.isNormal())
		{
			msg("<b>Nope! You can't.");
			return;
		}*/
		
		// MPerm
		if ( ! MPerm.getPermRel().has(msender, msenderFaction, true)) return;
		
		// Verify
		
		if (otherFaction == msenderFaction)
		{
			msg("<b>Nope! You can't declare a relation to yourself :)");
			return;
		}

		if (msenderFaction.getRelationWish(otherFaction) == newRelation)
		{
			msg("<b>You already have that relation wish set with %s.", otherFaction.getName());
			return;
		}
		
		// Event
		EventFactionsRelationChange event = new EventFactionsRelationChange(sender, msenderFaction, otherFaction, newRelation);
		event.run();
		if (event.isCancelled()) return;
		newRelation = event.getNewRelation();

		// try to set the new relation
		msenderFaction.setRelationWish(otherFaction, newRelation);
		Rel currentRelation = msenderFaction.getRelationTo(otherFaction, true);

		// if the relation change was successful
		if (newRelation == currentRelation)
		{
			otherFaction.msg("%s<i> is now %s.", msenderFaction.describeTo(otherFaction, true), newRelation.getDescFactionOne());
			msenderFaction.msg("%s<i> is now %s.", otherFaction.describeTo(msenderFaction, true), newRelation.getDescFactionOne());
		}
		// inform the other faction of your request
		else
		{
			otherFaction.msg("%s<i> wishes to be %s.", msenderFaction.describeTo(otherFaction, true), newRelation.getColor()+newRelation.getDescFactionOne());
			otherFaction.msg("<i>Type <c>/"+MConf.get().aliasesF.get(0)+" "+newRelation+" "+msenderFaction.getName()+"<i> to accept.");
			msenderFaction.msg("%s<i> were informed that you wish to be %s<i>.", otherFaction.describeTo(msenderFaction, true), newRelation.getColor()+newRelation.getDescFactionOne());
		}
		
		// TODO: The ally case should work!!
		//   * this might have to be bumped up to make that happen, & allow ALLY,NEUTRAL only
		if ( newRelation != Rel.TRUCE && otherFaction.getFlag(MFlag.getFlagPeaceful()))
		{
			otherFaction.msg("<i>This will have no effect while your faction is peaceful.");
			msenderFaction.msg("<i>This will have no effect while their faction is peaceful.");
		}
		
		if ( newRelation != Rel.TRUCE && msenderFaction.getFlag(MFlag.getFlagPeaceful()))
		{
			otherFaction.msg("<i>This will have no effect while their faction is peaceful.");
			msenderFaction.msg("<i>This will have no effect while your faction is peaceful.");
		}
	}
	
}
