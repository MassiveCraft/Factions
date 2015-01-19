package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.ps.PS;


public abstract class CmdFactionsSetX extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private String formatOne = null;
	public String getFormatOne() { return this.formatOne; }
	public void setFormatOne(String formatOne) { this.formatOne = formatOne; }
	
	private String formatMany = null;
	public String getFormatMany() { return this.formatMany; }
	public void setFormatMany(String formatMany) { this.formatMany = formatMany; }
	
	private boolean claim = true;
	public boolean isClaim() { return this.claim; }
	public void setClaim(boolean claim) { this.claim = claim; }
	
	private int factionArgIndex = 0;
	public int getFactionArgIndex() { return this.factionArgIndex; }
	public void setFactionArgIndex(int factionArgIndex) { this.factionArgIndex = factionArgIndex; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetX(boolean claim)
	{
		this.setClaim(claim);
	}
	
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
		msender.tryClaim(newFaction, chunks, this.getFormatOne(), this.getFormatMany());
	}
	
	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	
	public abstract Set<PS> getChunks();
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public Faction getNewFaction()
	{
		if (this.isClaim())
		{
			return this.arg(this.getFactionArgIndex(), ARFaction.get(), msenderFaction);
		}
		else
		{
			return FactionColl.get().getNone();
		}
	}
	
}
