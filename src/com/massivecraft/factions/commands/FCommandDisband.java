package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;


public class FCommandDisband extends FCommand
{
	public FCommandDisband()
	{
		super();
		this.aliases.add("disband");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction tag", "yours");
		
		this.permission = Permission.COMMAND_DISBAND.node;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		// The faction, default to your own.. but null if console sender.
		Faction faction = this.argAsFaction(0, fme == null ? null : myFaction);
		if (faction == null) return;
		
		boolean isMyFaction = fme == null ? false : faction == myFaction;
		
		if (isMyFaction)
		{
			if ( ! assertMinRole(Role.ADMIN)) return;
		}
		else
		{
			if ( ! Permission.COMMAND_DISBAND_ANY.has(me, true))
			{
				return;
			}
		}

		if (faction.isPermanent())
		{
			sendMessageParsed("<i>This faction is designated as permanent, so you cannot disband it.");
			return;
		}

		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			String who = senderIsConsole ? "A server admin" : fme.getNameAndRelevant(fplayer);
			if (fplayer.getFaction() == faction)
			{
				fplayer.sendMessageParsed("<h>%s<i> disbanded your faction.", who);
			}
			else
			{
				fplayer.sendMessageParsed("<h>%s<i> disbanded the faction %s.", who, faction.getTag(fplayer));
			}
		}
		
		if (Conf.bankEnabled)
		{
			double amount = faction.getMoney();
			Econ.addMoney(fme.getId(), amount); //Give all the faction's money to the disbander
			if (amount > 0.0)
			{
				String amountString = Econ.moneyString(amount);
				sendMessageParsed("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
				P.p.log(fme.getName() + " has been given bank holdings of "+amountString+" from disbanding "+faction.getTag()+".");
			}
		}		
		
		faction.detach();
		
		SpoutFeatures.updateAppearances();
	}
}
