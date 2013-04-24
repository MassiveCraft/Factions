package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.arg.ARDouble;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsPowerBoost extends FCommand
{
	public CmdFactionsPowerBoost()
	{
		this.addAliases("powerboost");
		
		this.addRequiredArg("faction");
		this.addRequiredArg("amount");
		
		this.addRequirements(ReqHasPerm.get(Perm.POWERBOOST.node));
	}
	
	@Override
	public void perform()
	{
		Faction faction = this.arg(0, ARFaction.get(fme));
		if (faction == null) return;
		
		Double amount = this.arg(1, ARDouble.get());
		if (amount == null) return;

		faction.setPowerBoost(amount);
	
		msg("<i>"+faction.getName()+" now has a power bonus/penalty of "+amount+" to min and max power levels.");
		
		// TODO: Inconsistent. Why is there no boolean to toggle this logging of?
		Factions.get().log(fme.getName()+" has set the power bonus/penalty for "+faction.getName()+" to "+amount+".");
	}
}
