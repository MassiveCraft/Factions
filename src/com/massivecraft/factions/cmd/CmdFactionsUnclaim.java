package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.event.FactionsEventLandUnclaim;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Faction;
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
		Faction otherFaction = BoardColl.get().getFactionAt(chunk);

		// FPerm
		if ( ! FPerm.TERRITORY.has(sender, otherFaction, true)) return;

		// Event
		FactionsEventLandUnclaim event = new FactionsEventLandUnclaim(sender, otherFaction, chunk);
		event.run();
		if (event.isCancelled()) return;
	
		//String moneyBack = "<i>";
		if (Econ.isEnabled())
		{
			double refund = Econ.calculateClaimRefund(myFaction.getLandCount());
			
			if(ConfServer.bankEnabled && ConfServer.bankFactionPaysLandCosts)
			{
				if ( ! Econ.modifyMoney(myFaction, refund, "unclaim this land")) return;
			}
			else
			{
				if ( ! Econ.modifyMoney(fme, refund, "unclaim this land")) return;
			}
		}

		BoardColl.get().removeAt(chunk);
		SpoutFeatures.updateTerritoryDisplayLoc(chunk);
		myFaction.msg("%s<i> unclaimed some land.", fme.describeTo(myFaction, true));

		if (ConfServer.logLandUnclaims)
		{
			Factions.get().log(fme.getName()+" unclaimed land at ("+chunk.getChunkX()+","+chunk.getChunkZ()+") from the faction: "+otherFaction.getTag());
		}
	}
	
}
