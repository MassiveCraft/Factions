package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.event.FactionsEventCreate;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
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
		// Args
		String newTag = this.arg(0);
		
		// Verify
		if (fme.hasFaction())
		{
			msg("<b>You must leave your current faction first.");
			return;
		}
		
		if (FactionColl.get().isTagTaken(newTag))
		{
			msg("<b>That tag is already in use.");
			return;
		}
		
		ArrayList<String> tagValidationErrors = FactionColl.validateTag(newTag);
		if (tagValidationErrors.size() > 0)
		{
			sendMessage(tagValidationErrors);
			return;
		}

		// Pre-Generate Id
		String factionId = FactionColl.get().getIdStrategy().generate(FactionColl.get());
		
		// Event
		FactionsEventCreate createEvent = new FactionsEventCreate(sender, newTag, factionId);
		createEvent.run();
		if (createEvent.isCancelled()) return;
		
		// Apply
		Faction faction = FactionColl.get().create(factionId);
		faction.setTag(newTag);
		
		fme.setRole(Rel.LEADER);
		fme.setFaction(faction);
		
		FactionsEventMembershipChange joinEvent = new FactionsEventMembershipChange(sender, fme, faction, MembershipChangeReason.CREATE);
		joinEvent.run();
		// NOTE: join event cannot be cancelled or you'll have an empty faction
		
		// Inform
		for (FPlayer follower : FPlayerColl.get().getAllOnline())
		{
			follower.msg("%s<i> created a new faction %s", fme.describeTo(follower, true), faction.getTag(follower));
		}
		
		msg("<i>You should now: %s", Factions.get().getOuterCmdFactions().cmdFactionsDescription.getUseageTemplate());

		if (ConfServer.logFactionCreate)
		{
			Factions.get().log(fme.getName()+" created a new faction: "+newTag);
		}
	}
	
}
