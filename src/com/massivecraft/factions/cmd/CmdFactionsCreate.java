package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasntFaction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.store.MStore;

public class CmdFactionsCreate extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsCreate()
	{
		// Aliases
		this.addAliases("create");

		// Args
		this.addRequiredArg("name");

		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasntFaction.get());
		this.addRequirements(ReqHasPerm.get(Perm.CREATE.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		String newName = this.arg(0);
		
		// Verify
		FactionColl coll = FactionColls.get().get(usender);
		
		if (coll.isNameTaken(newName))
		{
			msg("<b>That name is already in use.");
			return;
		}
		
		ArrayList<String> nameValidationErrors = coll.validateName(newName);
		if (nameValidationErrors.size() > 0)
		{
			sendMessage(nameValidationErrors);
			return;
		}

		// Pre-Generate Id
		String factionId = MStore.createId();
		
		// Event
		EventFactionsCreate createEvent = new EventFactionsCreate(sender, coll.getUniverse(), factionId, newName);
		createEvent.run();
		if (createEvent.isCancelled()) return;
		
		// Apply
		Faction faction = coll.create(factionId);
		faction.setName(newName);
		
		usender.setRole(Rel.LEADER);
		usender.setFaction(faction);
		
		EventFactionsMembershipChange joinEvent = new EventFactionsMembershipChange(sender, usender, faction, MembershipChangeReason.CREATE);
		joinEvent.run();
		// NOTE: join event cannot be cancelled or you'll have an empty faction
		
		// Inform
		for (UPlayer follower : UPlayerColls.get().get(usender).getAllOnline())
		{
			follower.msg("%s<i> created a new faction %s", usender.describeTo(follower, true), faction.getName(follower));
		}
		
		msg("<i>You should now: %s", Factions.get().getOuterCmdFactions().cmdFactionsDescription.getUseageTemplate());

		if (MConf.get().logFactionCreate)
		{
			Factions.get().log(usender.getName()+" created a new faction: "+newName);
		}
	}
	
}
