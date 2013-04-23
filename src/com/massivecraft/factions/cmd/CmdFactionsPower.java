package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsPower extends FCommand
{

	public CmdFactionsPower()
	{
		this.addAliases("power", "pow");

		this.addOptionalArg("player", "you");

		this.addRequirements(ReqHasPerm.get(Perm.POWER.node));
	}

	@Override
	public void perform()
	{
		UPlayer target = this.arg(0, ARUPlayer.getStartAny(fme), fme);
		if (target == null) return;

		if (target != fme && ! Perm.POWER_ANY.has(sender, true)) return;

		double powerBoost = target.getPowerBoost();
		String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
		msg("%s<a> - Power / Maxpower: <i>%d / %d %s", target.describeTo(fme, true), target.getPowerRounded(), target.getPowerMaxRounded(), boost);
	}

}
