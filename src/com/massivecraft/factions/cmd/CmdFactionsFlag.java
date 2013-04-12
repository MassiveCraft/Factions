package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsFlag extends FCommand
{
	
	public CmdFactionsFlag()
	{
		super();
		this.aliases.add("flag");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("flag", "all");
		this.optionalArgs.put("yes/no", "read");
		
		this.permission = Perm.FLAG.node;
		
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
		
		if ( ! this.argIsSet(1))
		{
			msg(Txt.titleize("Flags for " + faction.describeTo(fme, true)));
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
			msg(Txt.titleize("Flag for " + faction.describeTo(fme, true)));
			msg(flag.getStateInfo(faction.getFlag(flag), true));
			return;
		}
		
		Boolean targetValue = this.argAsBool(2);
		if (targetValue == null) return;

		// Do the sender have the right to change flags?
		if ( ! Perm.FLAG_SET.has(sender, true)) return;
		
		// Do the change
		msg(Txt.titleize("Flag for " + faction.describeTo(fme, true)));
		faction.setFlag(flag, targetValue);
		msg(flag.getStateInfo(faction.getFlag(flag), true));
	}
	
}
