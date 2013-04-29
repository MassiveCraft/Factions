package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.arg.ARBoolean;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsAccessFaction extends CmdFactionsAccessAbstract
{
	public CmdFactionsAccessFaction()
	{
		this.addAliases("f", "faction");
		
		this.addRequiredArg("faction");
		this.addOptionalArg("yes/no", "toggle");
		
		this.addRequirements(ReqHasPerm.get(Perm.ACCESS_FACTION.node));
	}

	@Override
	public void innerPerform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get(usender));
		if (faction == null) return;
		
		Boolean newValue = this.arg(1, ARBoolean.get(), !ta.isFactionIdGranted(faction.getId()));
		if (newValue == null) return;
		
		// FPerm
		if (!FPerm.ACCESS.has(usender, hostFaction, true)) return;
		
		// Apply
		ta = ta.withFactionId(faction.getId(), newValue);
		BoardColls.get().setTerritoryAccessAt(chunk, ta);
		
		// Inform
		this.sendAccessInfo();
	}
}
