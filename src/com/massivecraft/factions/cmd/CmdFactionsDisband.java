package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.event.FactionsEventDisband;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsDisband extends FCommand
{
	public CmdFactionsDisband()
	{
		this.addAliases("disband");
		
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.DISBAND.node));
	}
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get(), myFaction);
		if (faction == null) return;
		
		// FPerm
		if ( ! FPerm.DISBAND.has(sender, faction, true)) return;

		// Verify
		if (faction.getFlag(FFlag.PERMANENT))
		{
			msg("<i>This faction is designated as permanent, so you cannot disband it.");
			return;
		}

		// Event
		FactionsEventDisband event = new FactionsEventDisband(me, faction);
		event.run();
		if (event.isCancelled()) return;

		// Merged Apply and Inform
		
		// Send FPlayerLeaveEvent for each player in the faction
		for (FPlayer fplayer : faction.getFPlayers())
		{
			FactionsEventMembershipChange membershipChangeEvent = new FactionsEventMembershipChange(sender, fplayer, FactionColl.get().getNone(), MembershipChangeReason.DISBAND);
			membershipChangeEvent.run();
		}

		// Inform all players
		for (FPlayer fplayer : FPlayerColl.get().getAllOnline())
		{
			String who = fme.describeTo(fplayer);
			if (fplayer.getFaction() == faction)
			{
				fplayer.msg("<h>%s<i> disbanded your faction.", who);
			}
			else
			{
				fplayer.msg("<h>%s<i> disbanded the faction %s.", who, faction.getTag(fplayer));
			}
		}
		if (ConfServer.logFactionDisband)
			Factions.get().log("The faction "+faction.getTag()+" ("+faction.getId()+") was disbanded by "+(senderIsConsole ? "console command" : fme.getName())+".");

		if (Econ.isEnabled() && ! senderIsConsole)
		{
			//Give all the faction's money to the disbander
			double amount = Econ.getBalance(faction.getAccountId());
			Econ.transferMoney(fme, faction, fme, amount, false);
			
			if (amount > 0.0)
			{
				String amountString = Econ.moneyString(amount);
				msg("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
				Factions.get().log(fme.getName() + " has been given bank holdings of "+amountString+" from disbanding "+faction.getTag()+".");
			}
		}		
		
		faction.detach();

		SpoutFeatures.updateTitle(null, null);
		SpoutFeatures.updateCape(null, null);
	}
}
