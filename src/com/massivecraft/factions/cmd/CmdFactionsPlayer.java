package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsPlayer extends FCommand
{
	public CmdFactionsPlayer()
	{
		this.addAliases("p", "player");
		
		this.addOptionalArg("player", "you");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.PLAYER.node));
	}
	
	@Override
	public void perform()
	{
		// Args
		UPlayer target = this.arg(0, ARUPlayer.getStartAny(sender), usender);
		if (target == null) return;
		
		// TODO: Print info

		double powerBoost = target.getPowerBoost();
		String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
		
		msg("%s<a> - Power / Maxpower: <i>%.2f / %.2f %s", target.describeTo(usender, true), target.getPower(), target.getPowerMax(), boost);
	}
}