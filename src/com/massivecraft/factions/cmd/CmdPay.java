package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;


public class CmdPay extends FCommand
{
	public CmdPay()
	{
		this.aliases.add("pay");
		
		this.requiredArgs.add("faction");
		this.requiredArgs.add("amount");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.PAY.node;
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
		
		if ( ! Conf.bankMembersCanWithdraw && ! assertMinRole(Role.MODERATOR))
		{
			msg("<b>Only faction moderators or admins are able to pay another faction.");
			return;
		}
		
		Faction us = fme.getFaction();
		Faction them = this.argAsFaction(0);
		if ( them == null ) return;
		double amount = this.argAsDouble(1, 0d);

		if( amount > 0.0 )
		{
			String amountString = Econ.moneyString(amount);

			if( amount > us.getAccount().balance() )
			{
				amount = us.getAccount().balance();
			}
			
			us.getAccount().subtract(amount);
			them.getAccount().add(amount);
			
			msg("<i>You have paid "+amountString+" from "+us.getTag()+"'s bank to "+them.getTag()+"'s bank.");
			msg("<i>"+us.getTag()+" now has "+Econ.moneyString(us.getAccount().balance()));
			P.p.log(fme.getName() + " paid "+amountString+" from "+us.getTag()+"'s bank to "+them.getTag()+"'s bank.");
			
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				if (fplayer.getFaction() == us || fplayer.getFaction() == them)
				{
					fplayer.msg(fme.getNameAndRelevant(fplayer)+"<i> has sent "+amountString+" from "+us.getTag()+" to "+them.getTag());
				}
			}
		}
	}
}
