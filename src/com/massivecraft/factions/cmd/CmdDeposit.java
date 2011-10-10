package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;


public class CmdDeposit extends FCommand
{
	
	public CmdDeposit()
	{
		super();
		this.aliases.add("deposit");
		
		this.requiredArgs.add("amount");
		//this.optionalArgs
		
		this.permission = Permission.DEPOSIT.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! Conf.bankEnabled) return;
		
		Faction faction = myFaction;
		
		double amount = this.argAsDouble(0, 0);
				
		if( amount > 0.0 )
		{
			String amountString = Econ.moneyString(amount);
			
			if( ! Econ.deductMoney(fme.getName(), amount ) )
			{
				msg("<b>You cannot afford to deposit that much.");
			}
			else
			{
				faction.addMoney(amount);
				msg("<i>You have deposited <h>%s<i> into <h>%s's<i> bank.", amountString, faction.getTag());
				msg("%s<i> now has <h>%s", faction.getTag(fme), Econ.moneyString(faction.getMoney()));
				P.p.log(fme.getName() + " deposited "+amountString+" into "+faction.getTag()+"'s bank.");
				
				for (FPlayer fplayer : FPlayers.i.getOnline())
				{
					if (fplayer.getFaction() == faction)
					{
						fplayer.msg("%s<i> has deposited <h>%s", fme.getNameAndRelevant(fplayer), amountString);
					}
				}
			}
		}
	}
	
}
