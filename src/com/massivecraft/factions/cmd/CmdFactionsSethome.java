package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.FactionsEventHomeChange;
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
		
		// Args
		Faction faction = this.arg(0, ARFaction.get(myFaction), myFaction);
		if (faction == null) return;
		
		PS newHome = PS.valueOf(me.getLocation());
		
		// FPerm
		if ( ! FPerm.SETHOME.has(sender, faction, true)) return;
		
		// Verify
		if (!fme.isUsingAdminMode() && !faction.isValidHome(newHome))
		{
			fme.msg("<b>Sorry, your faction home can only be set inside your own claimed territory.");
			return;
		}
		
		// Event
		FactionsEventHomeChange event = new FactionsEventHomeChange(sender, faction, newHome);
		event.run();
		if (event.isCancelled()) return;
		newHome = event.getNewHome();

		// Apply
		faction.setHome(newHome);
		
		// Inform
		faction.msg("%s<i> set the home for your faction. You can now use:", fme.describeTo(myFaction, true));
		faction.sendMessage(Factions.get().getOuterCmdFactions().cmdFactionsHome.getUseageTemplate());
		if (faction != myFaction)
		{
			fme.msg("<b>You have set the home for the "+faction.getTag(fme)+"<i> faction.");
		}
	}
	
}
