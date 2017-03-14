package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;

public class CmdFactionsAccessPlayer extends CmdFactionsAccessAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAccessPlayer()
	{
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeBooleanYes.get(), "yes/no", "toggle");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void innerPerform() throws MassiveException
	{
		// Args
		MPlayer mplayer = this.readArg();
		boolean newValue = this.readArg(!ta.isPlayerIdGranted(mplayer.getId()));
		
		// MPerm
		if (!MPerm.getPermAccess().has(msender, hostFaction, true)) return;
		
		// Apply
		ta = ta.withPlayerId(mplayer.getId(), newValue);
		BoardColl.get().setTerritoryAccessAt(chunk, ta);
		
		// Inform
		this.sendAccessInfo();
	}
	
}
