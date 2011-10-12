package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;


public class CmdWithdraw extends FCommand
{
	
	public CmdWithdraw()
	{
		this.aliases.add("withdraw");
		
		this.requiredArgs.add("amount");
		//this.optionalArgs.put("factiontag", "yours");
		
		this.permission = Permission.WITHDRAW.node;
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
		
		Econ.transferMoney(fme, myFaction, fme, this.argAsDouble(0, 0));
		
		/*if ( ! Conf.bankMembersCanWithdraw && ! assertMinRole(Role.MODERATOR))
		{
			msg("<b>Only faction moderators or admins are able to withdraw from the bank.");
			return;
		}
		
		Faction faction = myFaction;
		
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
			msg("<i>You have withdrawn "+amountString+" from "+faction.getTag()+"'s bank.");
			msg("<i>"+faction.getTag()+" now has "+Econ.moneyString(faction.getMoney()));
			P.p.log(fme.getName() + " withdrew "+amountString+" from "+faction.getTag()+"'s bank.");
			
			// TODO: FAction.getOnlineMembers().
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				if (fplayer.getFaction() == faction)
				{
					fplayer.msg("%s<i> has withdrawn %s", fme.getNameAndRelevant(fplayer), amountString);
				}
			}
		}*/
	}
	
}
