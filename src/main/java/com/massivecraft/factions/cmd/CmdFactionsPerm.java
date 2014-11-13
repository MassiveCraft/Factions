package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARMPerm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARRel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPerm extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPerm()
	{
		// Aliases
		this.addAliases("perm");

		// Args
		this.addOptionalArg("faction", "you");
		this.addOptionalArg("perm", "all");
		this.addOptionalArg("relation", "read");
		this.addOptionalArg("yes/no", "read");
		this.setErrorOnToManyArgs(false);

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.PERM.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Arg: Faction
		Faction faction = this.arg(0, ARFaction.get(), msenderFaction);
		if (faction == null) return;
		
		// Case: Show All
		if ( ! this.argIsSet(1))
		{
			msg(Txt.titleize("Perms for " + faction.describeTo(msender, true)));
			msg(MPerm.getStateHeaders());
			for (MPerm perm : MPerm.getAll())
			{
				msg(perm.getStateInfo(faction.getPermitted(perm), true));
			}
			return;
		}
		
		// Arg: MPerm
		MPerm mperm = this.arg(1, ARMPerm.get());
		if (mperm == null) return;
		
		// Case: Show One
		if ( ! this.argIsSet(2))
		{
			msg(Txt.titleize("Perm for " + faction.describeTo(msender, true)));
			msg(MPerm.getStateHeaders());
			msg(mperm.getStateInfo(faction.getPermitted(mperm), true));
			return;
		}
		
		// Do the sender have the right to change perms for this faction?
		if ( ! MPerm.getPermPerms().has(msender, faction, true)) return;
		
		// Is this perm editable?
		if (!msender.isUsingAdminMode() && !mperm.isEditable())
		{
			msg("<b>The perm <h>%s <b>is not editable.", mperm.getName());
			return;
		}

		// Arg: Rel
		Rel rel = this.arg(2, ARRel.get());
		if (rel == null) return;
		
		if ( ! this.argIsSet(3))
		{
			msg("<b>Should <h>%s <b>have the <h>%s <b>permission or not?\nYou must <h>add \"yes\" or \"no\" <b>at the end.", Txt.getNicedEnum(rel), Txt.upperCaseFirst(mperm.getName()));
			return;
		}
		
		// Arg: Target Value
		Boolean targetValue = this.arg(3, ARBoolean.get(), null);
		if (targetValue == null) return;
		
		// Apply
		faction.setRelationPermitted(mperm, rel, targetValue);
		
		// The following is to make sure the leader always has the right to change perms if that is our goal.
		if (mperm == MPerm.getPermPerms() && MPerm.getPermPerms().getStandard().contains(Rel.LEADER))
		{
			faction.setRelationPermitted(MPerm.getPermPerms(), Rel.LEADER, true);
		}
		
		// Inform
		msg(Txt.titleize("Perm for " + faction.describeTo(msender, true)));
		msg(MPerm.getStateHeaders());
		msg(mperm.getStateInfo(faction.getPermitted(mperm), true));
	}
	
}
