package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.event.EventFactionsChunkChange;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsUnclaimall extends FCommand
{	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsUnclaimall()
	{
		// Aliases
		this.addAliases("unclaimall");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.UNCLAIM_ALL.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = usenderFaction;
		Faction newFaction = FactionColl.get().getNone();
		
		// FPerm
		if (!FPerm.TERRITORY.has(usender, faction, true)) return;

		// Apply
		Set<PS> chunks = BoardColl.get().getChunks(faction);
		int countTotal = chunks.size();
		int countSuccess = 0;
		int countFail = 0;
		for (PS chunk : chunks)
		{
			EventFactionsChunkChange event = new EventFactionsChunkChange(sender, chunk, newFaction);
			event.run();
			if (event.isCancelled())
			{
				countFail++;
			}
			else
			{
				countSuccess++;
				BoardColl.get().setFactionAt(chunk, newFaction);
			}
		}
		
		// Inform
		usenderFaction.msg("%s<i> unclaimed <h>%d <i>of your <h>%d <i>faction land. You now have <h>%d <i>land claimed.", usender.describeTo(usenderFaction, true), countSuccess, countTotal, countFail);

		// Log
		if (MConf.get().logLandUnclaims)
		{
			Factions.get().log(usender.getName()+" unclaimed everything for the faction: "+usenderFaction.getName());
		}
	}
	
}
