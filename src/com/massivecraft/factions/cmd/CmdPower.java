package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Perm;

public class CmdPower extends FCommand
{
	
	public CmdPower()
	{
		super();
		this.aliases.add("power");
		this.aliases.add("pow");
		
		//this.requiredArgs.add("faction tag");
		this.optionalArgs.put("player", "you");
		
		this.permission = Perm.POWER.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer target = this.argAsBestFPlayerMatch(0, fme);
		if (target == null) return;
		
		if (target != fme && ! Perm.POWER_ANY.has(sender, true)) return;

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostPower, "to show player power info", "for showing player power info")) return;

		double powerBoost = target.getPowerBoost();
		String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
		msg("%s<a> - Power / Maxpower: <i>%d / %d %s", target.describeTo(fme, true), target.getPowerRounded(), target.getPowerMaxRounded(), boost);
	}
	
}
