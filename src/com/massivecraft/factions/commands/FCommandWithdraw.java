package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;


public class FCommandWithdraw extends FCommand
{
	
	public FCommandWithdraw()
	{
		this.aliases.add("withdraw");
		
		this.requiredArgs.add("amount");
		//this.optionalArgs.put("factiontag", "yours");
		
		this.permission = Permission.COMMAND_WITHDRAW.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! Conf.bankEnabled) return;
		
		if ( ! Conf.bankMembersCanWithdraw && ! assertMinRole(Role.MODERATOR))
		{
			sendMessageParsed("<b>Only faction moderators or admins are able to withdraw from the bank.");
			return;
		}
		
		Faction faction = fme.getFaction();
		
		double amount = this.argAsDouble(0, 0d);
		
		if( amount > 0.0 )
		{
			String amountString = Econ.moneyString(amount);

			if( amount > faction.getMoney() )
			{
				amount = faction.getMoney();
			}
			
			// TODO: Improve messages.
			
			faction.removeMoney(amount);
			Econ.addMoney(fme.getName(), amount);
			sendMessageParsed("<i>You have withdrawn "+amountString+" from "+faction.getTag()+"'s bank.");
			sendMessageParsed("<i>"+faction.getTag()+" now has "+Econ.moneyString(faction.getMoney()));
			P.p.log(fme.getName() + " withdrew "+amountString+" from "+faction.getTag()+"'s bank.");
			
			// TODO: FAction.getOnlineMembers().
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				if (fplayer.getFaction() == faction)
				{
					fplayer.sendMessageParsed("%s<i> has withdrawn %s", fme.getNameAndRelevant(fplayer), amountString);
				}
			}
		}
	}
	
}
