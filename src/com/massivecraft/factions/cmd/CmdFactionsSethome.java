package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.event.FactionsHomeChangeEvent;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;

public class CmdFactionsSethome extends FCommand
{
	public CmdFactionsSethome()
	{
		this.addAliases("sethome");
		
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasPerm.get(Perm.SETHOME.node));
	}
	
	@Override
	public void perform()
	{
		// TODO: Make a command REQ instead?
		if ( ! ConfServer.homesEnabled)
		{
			fme.msg("<b>Sorry, Faction homes are disabled on this server.");
			return;
		}
		
		Faction faction = this.arg(0, ARFaction.get(), myFaction);
		if (faction == null) return;
		
		// Has faction permission?
		if ( ! FPerm.SETHOME.has(sender, faction, true)) return;
		
		PS newHome = PS.valueOf(me.getLocation());
		
		// Can the player set the faction home HERE?
		if (!fme.isUsingAdminMode() && !faction.isValidHome(newHome))
		{
			fme.msg("<b>Sorry, your faction home can only be set inside your own claimed territory.");
			return;
		}
		
		FactionsHomeChangeEvent event = new FactionsHomeChangeEvent(sender, FactionsHomeChangeEvent.REASON_COMMAND_SETHOME, faction, newHome);
		event.run();
		if (event.isCancelled()) return;
		newHome = event.getNewHome();

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(ConfServer.econCostSethome)) return;

		faction.setHome(newHome);
		
		faction.msg("%s<i> set the home for your faction. You can now use:", fme.describeTo(myFaction, true));
		faction.sendMessage(Factions.get().getOuterCmdFactions().cmdFactionsHome.getUseageTemplate());
		if (faction != myFaction)
		{
			fme.msg("<b>You have set the home for the "+faction.getTag(fme)+"<i> faction.");
		}
	}
	
}
