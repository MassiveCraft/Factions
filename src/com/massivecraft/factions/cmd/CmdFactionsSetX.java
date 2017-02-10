package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.MassiveException;
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
		this.setSetupEnabled(false);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{	
		// Args
		final Faction newFaction = this.getNewFaction();
		final Set<PS> chunks = this.getChunks();
		
		// Apply / Inform
		msender.tryClaim(newFaction, chunks, this.getFormatOne(), this.getFormatMany());
	}
	
	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	
	public abstract Set<PS> getChunks() throws MassiveException;
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public Faction getNewFaction() throws MassiveException
	{
		if (this.isClaim())
		{
			return this.readArgAt(this.getFactionArgIndex(), msenderFaction);
		}
		else
		{
			return FactionColl.get().getNone();
		}
	}
	
}
