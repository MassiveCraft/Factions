package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.Lang;

public class CmdFlag extends FCommand
{
	
	public CmdFlag()
	{
		super();
		this.aliases.add("flag");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("flag", "all");
		this.optionalArgs.put("yes/no", "read");
		
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
		if (faction == null) 
		{
			if (senderIsConsole)
			{
				msg(Lang.commandToFewArgs);
				sender.sendMessage(this.getUseageTemplate());
			}
			return;
		}
		
		if ( ! this.argIsSet(1))
		{
			msg(p.txt.titleize("Flags for " + faction.describeTo(fme, true)));
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
			msg(p.txt.titleize("Flag for " + faction.describeTo(fme, true)));
			msg(flag.getStateInfo(faction.getFlag(flag), true));
			return;
		}
		
		Boolean targetValue = this.argAsBool(2);
		if (targetValue == null) return;

		// Do the sender have the right to change flags?
		if ( ! Permission.FLAG_SET.has(sender, true)) return;
		
		// Do the change
		msg(p.txt.titleize("Flag for " + faction.describeTo(fme, true)));
		faction.setFlag(flag, targetValue);
		msg(flag.getStateInfo(faction.getFlag(flag), true));
	}
	
}
