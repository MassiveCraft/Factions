package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsPerm extends FCommand
{
	
	public CmdFactionsPerm()
	{
		super();
		
		this.addAliases("perm");
		
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("perm", "all");
		this.optionalArgs.put("relation", "read");
		this.optionalArgs.put("yes/no", "read");
		
		this.addRequirements(ReqHasPerm.get(Perm.PERM.node));
		
		this.errorOnToManyArgs = false;
	}
	
	@Override
	public void perform()
	{
		Faction faction = myFaction;
		if (this.argIsSet(0))
		{
			faction = this.argAsFaction(0);
		}
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
		
		FPerm perm = this.argAsFactionPerm(1);
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
		
		Rel rel = this.argAsRel(2);
		if (rel == null) return;
		
		Boolean val = this.argAsBool(3, null);
		if (val == null) return;
		
		// Do the change
		faction.setRelationPermitted(perm, rel, val);
		
		// The following is to make sure the leader always has the right to change perms if that is our goal.
		if (perm == FPerm.PERMS && FPerm.PERMS.getDefault().contains(Rel.LEADER))
		{
			faction.setRelationPermitted(FPerm.PERMS, Rel.LEADER, true);
		}
		
		msg(Txt.titleize("Perm for " + faction.describeTo(fme, true)));
		msg(FPerm.getStateHeaders());
		msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
	}
	
}
