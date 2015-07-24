package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.massivecore.cmd.VisibilityMode;
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
	
		// Args
		this.addArg(ARMPlayer.get(), "player");
		this.addArg(ARFaction.get(), "faction", "their");
		
		// VisibilityMode
		this.setVisibilityMode(VisibilityMode.INVISIBLE);
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
