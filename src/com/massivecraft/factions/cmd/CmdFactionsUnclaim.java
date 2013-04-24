package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.event.FactionsEventChunkChange;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;

public class CmdFactionsUnclaim extends FCommand
{
	public CmdFactionsUnclaim()
	{
		this.addAliases("unclaim", "declaim");
		
		this.addRequirements(ReqHasPerm.get(Perm.UNCLAIM.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform()
	{
		// Args
		PS chunk = PS.valueOf(me).getChunk(true);
		Faction otherFaction = BoardColls.get().getFactionAt(chunk);

		Faction newFaction = FactionColls.get().get(me).getNone();
		
		// FPerm
		// TODO: Recode so that pillage is possible
		if ( ! FPerm.TERRITORY.has(sender, otherFaction, true)) return;

		// Event
		FactionsEventChunkChange event = new FactionsEventChunkChange(sender, chunk, newFaction);
		event.run();
		if (event.isCancelled()) return;

		// Apply
		BoardColls.get().setFactionAt(chunk, newFaction);
		
		// Inform
		myFaction.msg("%s<i> unclaimed some land.", fme.describeTo(myFaction, true));

		if (MConf.get().logLandUnclaims)
		{
			Factions.get().log(fme.getName()+" unclaimed land at ("+chunk.getChunkX()+","+chunk.getChunkZ()+") from the faction: "+otherFaction.getTag());
		}
	}
	
}
