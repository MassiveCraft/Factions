package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFPerm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARRel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.arg.ARBoolean;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsPerm extends FCommand
{
	public CmdFactionsPerm()
	{
		this.addAliases("perm");

		this.addOptionalArg("faction", "you");
		this.addOptionalArg("perm", "all");
		this.addOptionalArg("relation", "read");
		this.addOptionalArg("yes/no", "read");
		this.setErrorOnToManyArgs(false);

		this.addRequirements(ReqHasPerm.get(Perm.PERM.node));
	}

	@Override
	public void perform()
	{
		Faction faction = this.arg(0, ARFaction.get(myFaction), myFaction);
		if (faction == null) return;

		if ( ! this.argIsSet(1))
		{
			msg(Txt.titleize("Perms for " + faction.describeTo(fme, true)));
			msg(FPerm.getStateHeaders());
			for (FPerm perm : FPerm.values())
			{
				msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
			}
			return;
		}

		FPerm perm = this.arg(1, ARFPerm.get());
		if (perm == null) return;

		if ( ! this.argIsSet(2))
		{
			msg(Txt.titleize("Perm for " + faction.describeTo(fme, true)));
			msg(FPerm.getStateHeaders());
			msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
			return;
		}

		// Do the sender have the right to change perms for this faction?
		if ( ! FPerm.PERMS.has(sender, faction, true)) return;

		Rel rel = this.arg(2, ARRel.get());
		if (rel == null) return;

		Boolean val = this.arg(3, ARBoolean.get(), null);
		if (val == null) return;

		// Do the change
		faction.setRelationPermitted(perm, rel, val);

		// The following is to make sure the leader always has the right to change perms if that is our goal.
		if (perm == FPerm.PERMS && FPerm.PERMS.getDefault(faction).contains(Rel.LEADER))
		{
			faction.setRelationPermitted(FPerm.PERMS, Rel.LEADER, true);
		}

		msg(Txt.titleize("Perm for " + faction.describeTo(fme, true)));
		msg(FPerm.getStateHeaders());
		msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
	}

}
