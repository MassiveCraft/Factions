package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;


public class CmdMoneyDeposit extends FCommand
{
	
	public CmdMoneyDeposit()
	{
		super();
		this.aliases.add("d");
		this.aliases.add("deposit");
		
		this.requiredArgs.add("amount");
		this.optionalArgs.put("faction", "yours");
		
		this.permission = Permission.MONEY_DEPOSIT.node;
		this.setHelpShort("deposit money");
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		double amount = this.argAsDouble(0, 0d);
		Faction faction = this.argAsFaction(1, myFaction);
		if (faction == null) return;
		if (!Conf.bankCanBeUsedEverywhere) {
			FLocation floc = new FLocation(fme.getPlayer().getLocation());
			Faction otherfaction = Board.getFactionAt(floc);
			if (faction.getLandRounded() > 0) { // If you have more than 1 territory (= if you have a faction && if you have more than 1 claimed chunk)
				if (faction != otherfaction) { // If you're not in your territory
					msg("<b>You can't deposit unless you are on your faction's territory.");
					return;
				}
			}
		}
		Econ.transferMoney(fme, fme, faction, amount);
	}
	
}
