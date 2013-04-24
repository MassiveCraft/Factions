package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
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
		this.addAliases("unclaimall", "declaimall");
		
		this.addRequirements(ReqHasPerm.get(Perm.UNCLAIM_ALL.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = myFaction;
		Faction newFaction = FactionColls.get().get(faction).getNone();
		
		// FPerm
		if (!FPerm.TERRITORY.has(sender, faction, true)) return;

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
		myFaction.msg("%s<i> unclaimed <h>5 <i> of your <h>200 <i>faction land. You now have <h>23 <i>land left.", fme.describeTo(myFaction, true), countSuccess, countTotal, countFail);

		// Log
		if (MConf.get().logLandUnclaims)
		{
			Factions.get().log(fme.getName()+" unclaimed everything for the faction: "+myFaction.getName());
		}
	}
	
}
