package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.cmd.type.TypeRank;
import com.massivecraft.factions.cmd.type.TypeRankName;
import com.massivecraft.massivecore.MassiveException;

public class CmdFactionsRankCreate extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankCreate()
	{
		// Parameters
		this.addParameter(TypeRankName.getStrict(), "name");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		String name = this.readArg();
		
		// Create
		Rank rank = msenderFaction.createRank(name);
		
		// Add
		msenderFaction.addRank(rank);
		
		// Inform
		message(mson(
			"The rank ",
			TypeRank.get().getVisualMson(rank),
			" has been created."
		).color(ChatColor.YELLOW));
	}
	
}
