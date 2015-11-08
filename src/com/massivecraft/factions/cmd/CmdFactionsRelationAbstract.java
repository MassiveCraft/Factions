package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsRelationChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.util.Txt;

public abstract class CmdFactionsRelationAbstract extends FactionsCommand
{
	public Rel targetRelation;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRelationAbstract()
	{
		// Aliases
		this.addParameter(TypeFaction.get(), "faction");

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.RELATION.node));
		this.addRequirements(ReqHasFaction.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Faction otherFaction = this.readArg();
		
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
			MassiveCommand relationshipCommand = null;
			if (newRelation.equals(Rel.NEUTRAL)) relationshipCommand = Factions.get().getOuterCmdFactions().cmdFactionsRelationNeutral;
			else if (newRelation.equals(Rel.TRUCE)) relationshipCommand = Factions.get().getOuterCmdFactions().cmdFactionsRelationTruce;
			else if (newRelation.equals(Rel.ALLY)) relationshipCommand = Factions.get().getOuterCmdFactions().cmdFactionsRelationAlly;
			else if (newRelation.equals(Rel.ENEMY)) relationshipCommand = Factions.get().getOuterCmdFactions().cmdFactionsRelationEnemy;
			
			String command = relationshipCommand.getCommandLine(msenderFaction.getName());
			String tooltip = Txt.parse("<g>Click to <c>%s<i>.", command);
			
			// Mson creation
			Mson factionsRelationshipChange = mson(
				Mson.parse("%s<i> wishes to be %s. ", msenderFaction.describeTo(otherFaction, true), newRelation.getColor()+newRelation.getDescFactionOne()),
				mson(tooltip).tooltipParse(tooltip).command(command)
			);
			
			otherFaction.sendMessage(factionsRelationshipChange);
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
		
		// Mark as changed
		msenderFaction.changed();
	}
	
}
