package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFPerm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARRel;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPerm extends FCommand
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
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.PERM.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		Faction faction = this.arg(0, ARFaction.get(usenderFaction), usenderFaction);
		if (faction == null) return;
		
		if ( ! this.argIsSet(1))
		{
			msg(Txt.titleize("Perms for " + faction.describeTo(usender, true)));
			msg(FPerm.getStateHeaders());
			for (FPerm perm : FPerm.values())
			{
				msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
			}
			return;
		}
		
		FPerm perm = this.arg(1, ARFPerm.get());
		if (perm == null) return;
		//System.out.println("perm = "+perm);
		
		if ( ! this.argIsSet(2))
		{
			msg(Txt.titleize("Perm for " + faction.describeTo(usender, true)));
			msg(FPerm.getStateHeaders());
			msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
			return;
		}
		
		// Do the sender have the right to change perms for this faction?
		if ( ! FPerm.PERMS.has(usender, faction, true)) return;
		
		Rel rel = this.arg(2, ARRel.get());
		if (rel == null) return;
		
		if (!this.argIsSet(3))
		{
			msg("<b>Should <h>%s <b>have the <h>%s <b>permission or not?\nYou must <h>add \"yes\" or \"no\" <b>at the end.", Txt.getNicedEnum(rel), Txt.getNicedEnum(perm));
			return;
		}
		
		Boolean val = this.arg(3, ARBoolean.get(), null);
		if (val == null) return;
		
		// Do the change
		//System.out.println("setRelationPermitted perm "+perm+", rel "+rel+", val "+val);
		faction.setRelationPermitted(perm, rel, val);
		
		// The following is to make sure the leader always has the right to change perms if that is our goal.
		if (perm == FPerm.PERMS && FPerm.PERMS.getDefault(faction).contains(Rel.LEADER))
		{
			faction.setRelationPermitted(FPerm.PERMS, Rel.LEADER, true);
		}
		
		msg(Txt.titleize("Perm for " + faction.describeTo(usender, true)));
		msg(FPerm.getStateHeaders());
		msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
	}
	
}
