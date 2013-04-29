package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.event.FactionsEventChunkChange;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.ps.PS;

public class CmdFactionsUnclaimall extends FCommand
{	
	public CmdFactionsUnclaimall()
	{
		this.addAliases("unclaimall");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.UNCLAIM_ALL.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = usenderFaction;
		Faction newFaction = FactionColls.get().get(faction).getNone();
		
		// FPerm
		if (!FPerm.TERRITORY.has(usender, faction, true)) return;

		// Apply
		BoardColl boardColl = BoardColls.get().get(faction);
		Set<PS> chunks = boardColl.getChunks(faction);
		int countTotal = chunks.size();
		int countSuccess = 0;
		int countFail = 0;
		for (PS chunk : chunks)
		{
			FactionsEventChunkChange event = new FactionsEventChunkChange(sender, chunk, newFaction);
			event.run();
			if (event.isCancelled())
			{
				countFail++;
			}
			else
			{
				countSuccess++;
				boardColl.setFactionAt(chunk, newFaction);
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
