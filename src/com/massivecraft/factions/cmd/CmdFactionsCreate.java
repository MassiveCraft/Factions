package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.FPlayerColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
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
		
		FactionColl coll = FactionColls.get().get(fme);
		
		if (coll.isTagTaken(newTag))
		{
			msg("<b>That tag is already in use.");
			return;
		}
		
		ArrayList<String> tagValidationErrors = coll.validateTag(newTag);
		if (tagValidationErrors.size() > 0)
		{
			sendMessage(tagValidationErrors);
			return;
		}

		// Pre-Generate Id
		String factionId = coll.getIdStrategy().generate(coll);
		
		// Event
		FactionsEventCreate createEvent = new FactionsEventCreate(sender, coll.getUniverse(), factionId, newTag);
		createEvent.run();
		if (createEvent.isCancelled()) return;
		
		// Apply
		Faction faction = coll.create(factionId);
		faction.setTag(newTag);
		
		fme.setRole(Rel.LEADER);
		fme.setFaction(faction);
		
		FactionsEventMembershipChange joinEvent = new FactionsEventMembershipChange(sender, fme, faction, MembershipChangeReason.CREATE);
		joinEvent.run();
		// NOTE: join event cannot be cancelled or you'll have an empty faction
		
		// Inform
		for (FPlayer follower : FPlayerColls.get().get(fme).getAllOnline())
		{
			follower.msg("%s<i> created a new faction %s", fme.describeTo(follower, true), faction.getTag(follower));
		}
		
		msg("<i>You should now: %s", Factions.get().getOuterCmdFactions().cmdFactionsDescription.getUseageTemplate());

		if (MConf.get().logFactionCreate)
		{
			Factions.get().log(fme.getName()+" created a new faction: "+newTag);
		}
	}
	
}
