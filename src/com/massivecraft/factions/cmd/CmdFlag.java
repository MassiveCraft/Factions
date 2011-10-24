package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdFlag extends FCommand
{
	
	public CmdFlag()
	{
		super();
		this.aliases.add("flag");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("flag", "all");
		this.optionalArgs.put("on/off", "read");
		
		this.permission = Permission.FLAG.node;
		this.disableOnLock = true;
		
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
		
		msg(p.txt.titleize("Flag(s) for " + faction.describeTo(fme)));
		
		if ( ! this.argIsSet(1))
		{
			for (FFlag flag : FFlag.values())
			{
				msg(flag.getStateInfo(faction.getFlag(flag), true));
			}
			return;
		}
		
		FFlag flag = this.argAsFactionFlag(1);
		if (flag == null) return;
		if ( ! this.argIsSet(2))
		{
			msg(flag.getStateInfo(faction.getFlag(flag), true));
			return;
		}
		
		Boolean targetValue = this.argAsBool(2);
		if (targetValue == null) return;

		// Do the sender have the right to change flags for this faction?
		if (Permission.FLAG_ANY.has(sender))
		{
			// This sender may modify any flag for anyone
		}
		else if ( ! flag.isChangeable())
		{
			msg("<b>Only server operators can change this flag.");
			return;
		}
		else if (faction != myFaction)
		{
			msg("<b>You are not a member in that faction.");
			return;
		}
		else if (fme.getRole().isLessThan(Rel.OFFICER))
		{
			msg("<b>You must be faction leader or officer to change your faction flags.");
			return;
		}
		
		// Do the change
		faction.setFlag(flag, targetValue);
		msg(flag.getStateInfo(faction.getFlag(flag), true));
	}
	
}
