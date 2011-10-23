package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FactionPerm;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdPerm extends FCommand
{
	
	public CmdPerm()
	{
		super();
		this.aliases.add("perm");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("perm", "all");
		this.optionalArgs.put("relationdelta", "read");
		
		this.permission = Permission.PERM.node;
		this.disableOnLock = true;
		
		this.errorOnToManyArgs = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
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
		
		msg(p.txt.titleize("Perm(s) for " + faction.describeTo(fme)));
		
		if ( ! this.argIsSet(1))
		{
			for (FactionPerm perm : FactionPerm.values())
			{
				msg(perm.getStateInfo(faction.getPerm(perm), true));
			}
			return;
		}
		
		FactionPerm perm = this.argAsFactionPerm(1);
		if (perm == null) return;
		if ( ! this.argIsSet(2))
		{
			msg(perm.getStateInfo(faction.getPerm(perm), true));
			return;
		}
		
		// TODO: Awesomesause parser for deltas...
		Set<Rel> targetValue = FactionPerm.parseRelDeltas(TextUtil.implode(args.subList(2, args.size()), " "), faction.getPerm(perm));

		// Do the sender have the right to change perms for this faction?
		if (Permission.PERM_ANY.has(sender))
		{
			// This sender may modify any perm for anyone
		}
		else if (faction != myFaction)
		{
			msg("<b>You are not a member in that faction.");
			return;
		}
		else if (fme.getRole().isLessThan(Rel.OFFICER))
		{
			msg("<b>You must be faction leader or officer to change your faction permissions.");
			return;
		}
		
		// Do the change
		faction.setPerm(perm, targetValue);
		msg(perm.getStateInfo(faction.getPerm(perm), true));
	}
	
}
