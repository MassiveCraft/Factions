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
import com.massivecraft.factions.event.FactionsEventJoin;
import com.massivecraft.factions.event.FactionsEventCreate;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsCreate extends FCommand
{
	public CmdFactionsCreate()
	{
		this.addAliases("create");
		
		this.addRequiredArg("faction tag");
		
		this.addRequirements(ReqHasPerm.get(Perm.CREATE.node));
	}
	
	@Override
	public void perform()
	{
		String tag = this.arg(0);
		
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

		// trigger the faction creation event (cancellable)
		String factionId = FactionColl.get().getIdStrategy().generate(FactionColl.get());
		
		FactionsEventCreate createEvent = new FactionsEventCreate(sender, tag, factionId);
		Bukkit.getServer().getPluginManager().callEvent(createEvent);
		if (createEvent.isCancelled()) return;
		
		Faction faction = FactionColl.get().create(factionId);

		// finish setting up the Faction
		faction.setTag(tag);
		
		// trigger the faction join event for the creator
		FactionsEventJoin joinEvent = new FactionsEventJoin(sender, fme, faction, FactionsEventJoin.PlayerJoinReason.CREATE);
		joinEvent.run();
		// NOTE: join event cannot be cancelled or you'll have an empty faction
		
		// finish setting up the FPlayer
		fme.setRole(Rel.LEADER);
		fme.setFaction(faction);

		for (FPlayer follower : FPlayerColl.get().getAllOnline())
		{
			follower.msg("%s<i> created a new faction %s", fme.describeTo(follower, true), faction.getTag(follower));
		}
		
		msg("<i>You should now: %s", Factions.get().getOuterCmdFactions().cmdFactionsDescription.getUseageTemplate());

		if (ConfServer.logFactionCreate)
			Factions.get().log(fme.getName()+" created a new faction: "+tag);
	}
	
}
