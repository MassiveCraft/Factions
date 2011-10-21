package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdPower extends FCommand
{
	
	public CmdPower()
	{
		super();
		this.aliases.add("power");
		this.aliases.add("pow");
		
		//this.requiredArgs.add("faction tag");
		this.optionalArgs.put("player name", "you");
		
		this.permission = Permission.POWER.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer target = this.argAsBestFPlayerMatch(0, fme);
		if (target == null) return;
		
		if (target != me && ! Permission.POWER_ANY.has(sender, true)) return;

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostPower, "to show player power info", "for showing player power info")) return;

		msg("%s<a> - Power / Maxpower: <i>%d / %d", target.describeTo(fme, true), target.getPowerRounded(), target.getPowerMaxRounded());
	}
	
}
