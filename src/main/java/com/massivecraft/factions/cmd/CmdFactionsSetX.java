package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;


public abstract class CmdFactionsSetX extends FactionsCommand
{
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{	
		// Args
		final Faction newFaction = this.getNewFaction();
		if (newFaction == null) return;
		
		final Set<PS> chunks = this.getChunks();
		if (chunks == null) return;
		
		// Apply / Inform
		msender.tryClaim(newFaction, chunks);
	}
	
	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	
	public abstract int getFactionArgIndex();
	
	public abstract Set<PS> getChunks();
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public Faction getNewFaction()
	{
		return this.arg(this.getFactionArgIndex(), ARFaction.get(), msenderFaction);
	}
	
}
