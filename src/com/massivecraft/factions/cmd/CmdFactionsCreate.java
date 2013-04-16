package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;

public class CmdFactionsCreate extends FCommand
{
	public CmdFactionsCreate()
	{
		super();
		
		this.addAliases("create");
		
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("", "");
		
		this.addRequirements(ReqHasPerm.get(Perm.CREATE.node));
	}
	
	@Override
	public void perform()
	{
		String tag = this.argAsString(0);
		
		if (fme.hasFaction())
		{
			msg("<b>You must leave your current faction first.");
			return;
		}
		
		if (FactionColl.get().isTagTaken(tag))
		{
			msg("<b>That tag is already in use.");
			return;
		}
		
		ArrayList<String> tagValidationErrors = FactionColl.validateTag(tag);
		if (tagValidationErrors.size() > 0)
		{
			sendMessage(tagValidationErrors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if ( ! canAffordCommand(ConfServer.econCostCreate, "to create a new faction")) return;

		// trigger the faction creation event (cancellable)
		String factionId = FactionColl.get().getIdStrategy().generate(FactionColl.get());
		
		FactionCreateEvent createEvent = new FactionCreateEvent(sender, tag, factionId);
		Bukkit.getServer().getPluginManager().callEvent(createEvent);
		if(createEvent.isCancelled()) return;
		
		// then make 'em pay (if applicable)
		if ( ! payForCommand(ConfServer.econCostCreate, "to create a new faction", "for creating a new faction")) return;
		
		Faction faction = FactionColl.get().create(factionId);

		// TODO: Why would this even happen??? Auto increment clash??
		if (faction == null)
		{
			msg("<b>There was an internal error while trying to create your faction. Please try again.");
			return;
		}

		// finish setting up the Faction
		faction.setTag(tag);
		
		// trigger the faction join event for the creator
		FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayerColl.get().get(sender),faction,FPlayerJoinEvent.PlayerJoinReason.CREATE);
		Bukkit.getServer().getPluginManager().callEvent(joinEvent);
		// join event cannot be cancelled or you'll have an empty faction
		
		// finish setting up the FPlayer
		fme.setRole(Rel.LEADER);
		fme.setFaction(faction);

		for (FPlayer follower : FPlayerColl.get().getAllOnline())
		{
			follower.msg("%s<i> created a new faction %s", fme.describeTo(follower, true), faction.getTag(follower));
		}
		
		msg("<i>You should now: %s", p.cmdBase.cmdFactionsDescription.getUseageTemplate());

		if (ConfServer.logFactionCreate)
			Factions.get().log(fme.getName()+" created a new faction: "+tag);
	}
	
}
