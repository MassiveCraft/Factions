package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.util.MUtil;

public class CmdFactionsRankOld extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
		
	public final String rankName;
		
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankOld(String rank)
	{
		// Fields
		this.rankName = rank.toLowerCase();
		
		// Aliases
		this.addAliases(rankName);
	
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeFaction.get(), "faction", "their");
		
		// Visibility
		this.setVisibility(Visibility.INVISIBLE);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		Factions.get().getOuterCmdFactions().cmdFactionsRank.execute(sender, MUtil.list(this.argAt(0), this.rankName, this.argAt(1)));
	}
	
}
