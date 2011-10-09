package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;


public class FCommandDeposit extends FCommand
{
	
	public FCommandDeposit()
	{
		super();
		this.aliases.add("deposit");
		
		this.requiredArgs.add("amount");
		//this.optionalArgs
		
		this.permission = Permission.COMMAND_DEPOSIT.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! Conf.bankEnabled) return;
		
		Faction faction = fme.getFaction();
		
		double amount = this.argAsDouble(0, 0);
				
		if( amount > 0.0 )
		{
			String amountString = Econ.moneyString(amount);
			
			if( ! Econ.deductMoney(fme.getName(), amount ) )
			{
				sendMessageParsed("<b>You cannot afford to deposit that much.");
			}
			else
			{
				faction.addMoney(amount);
				sendMessage("You have deposited "+amountString+" into "+faction.getTag()+"'s bank.");
				sendMessage(faction.getTag()+" now has "+Econ.moneyString(faction.getMoney()));
				P.p.log(fme.getName() + " deposited "+amountString+" into "+faction.getTag()+"'s bank.");
				
				for (FPlayer fplayer : FPlayers.i.getOnline())
				{
					if (fplayer.getFaction() == faction)
					{
						fplayer.sendMessageParsed("%s has deposited %s", fme.getNameAndRelevant(fplayer), amountString);
					}
				}
			}
		}
	}
	
}
