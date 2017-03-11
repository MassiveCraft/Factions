package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.cmd.type.TypeRank;
import com.massivecraft.factions.cmd.type.TypeRankName;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mson.Mson;

public class CmdFactionsRankName extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankName()
	{
		// Parameters
		this.addParameter(TypeRank.get(), "rank");
		this.addParameter(TypeRankName.getLenient(), "name", "show");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		Rank rank = this.readArg();
		Rank oldRank = rank.copy();
		
		// Are we showing?
		if (!this.argIsSet(1))
		{
			this.inform(rank, false);
			return;
		}
		
		String name = this.readArg();
		
		// Change
		rank.setName(name);
		
		// Add
		msenderFaction.addRank(rank);
		
		// Inform
		this.inform(oldRank, true);
	}
	
	private void inform(Rank rank, boolean updated)
	{
		message(mson(
				"The rank name of ",
				TypeRank.get().getVisualMson(rank),
				" is ",
				updated ? "now " : Mson.EMPTY,
				mson(String.valueOf(rank.getOrder())).color(ChatColor.LIGHT_PURPLE)
		).color(ChatColor.YELLOW));
	}
	
}
