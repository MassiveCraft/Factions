package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFFlag;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.arg.ARBoolean;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsFlag extends FCommand
{

	public CmdFactionsFlag()
	{
		this.addAliases("flag");

		this.addOptionalArg("faction", "you");
		this.addOptionalArg("flag", "all");
		this.addOptionalArg("yes/no", "read");

		this.addRequirements(ReqHasPerm.get(Perm.FLAG.node));
	}

	@Override
	public void perform()
	{
		Faction faction = this.arg(0, ARFaction.get(sender), myFaction);
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

		FFlag flag = this.arg(1, ARFFlag.get());
		if (flag == null) return;

		if ( ! this.argIsSet(2))
		{
			msg(Txt.titleize("Flag for " + faction.describeTo(fme, true)));
			msg(flag.getStateInfo(faction.getFlag(flag), true));
			return;
		}

		Boolean targetValue = this.arg(2, ARBoolean.get());
		if (targetValue == null) return;

		// Do the sender have the right to change flags?
		if ( ! Perm.FLAG_SET.has(sender, true)) return;

		// Do the change
		msg(Txt.titleize("Flag for " + faction.describeTo(fme, true)));
		faction.setFlag(flag, targetValue);
		msg(flag.getStateInfo(faction.getFlag(flag), true));
	}

}
