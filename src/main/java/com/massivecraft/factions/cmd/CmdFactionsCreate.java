package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasntFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.store.MStore;

public class CmdFactionsCreate extends FactionsCommand
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
		if (FactionColl.get().isNameTaken(newName))
		{
			msg("<b>That name is already in use.");
			return;
		}
		
		ArrayList<String> nameValidationErrors = FactionColl.get().validateName(newName);
		if (nameValidationErrors.size() > 0)
		{
			sendMessage(nameValidationErrors);
			return;
		}

		// Pre-Generate Id
		String factionId = MStore.createId();
		
		// Event
		EventFactionsCreate createEvent = new EventFactionsCreate(sender, factionId, newName);
		createEvent.run();
		if (createEvent.isCancelled()) return;
		
		// Apply
		Faction faction = FactionColl.get().create(factionId);
		faction.setName(newName);
		
		msender.setRole(Rel.LEADER);
		msender.setFaction(faction);
		
		EventFactionsMembershipChange joinEvent = new EventFactionsMembershipChange(sender, msender, faction, MembershipChangeReason.CREATE);
		joinEvent.run();
		// NOTE: join event cannot be cancelled or you'll have an empty faction
		
		// Inform
		msg("<i>You created the faction %s", faction.getName(msender));
		msg("<i>You should now: %s", Factions.get().getOuterCmdFactions().cmdFactionsDescription.getUseageTemplate());

		// Log
		if (MConf.get().logFactionCreate)
		{
			Factions.get().log(msender.getName()+" created a new faction: "+newName);
		}
	}
	
}
