package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsAccessFaction extends CmdFactionsAccessAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAccessFaction()
	{
		// Aliases
		this.addAliases("f", "faction");
		
		// Args
		this.addRequiredArg("faction");
		this.addOptionalArg("yes/no", "toggle");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.ACCESS_FACTION.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void innerPerform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get());
		if (faction == null) return;
		
		Boolean newValue = this.arg(1, ARBoolean.get(), !ta.isFactionIdGranted(faction.getId()));
		if (newValue == null) return;
		
		// MPerm
		if (!MPerm.getPermAccess().has(msender, hostFaction, true)) return;
		
		// Apply
		ta = ta.withFactionId(faction.getId(), newValue);
		BoardColl.get().setTerritoryAccessAt(chunk, ta);
		
		// Inform
		this.sendAccessInfo();
	}
	
}
