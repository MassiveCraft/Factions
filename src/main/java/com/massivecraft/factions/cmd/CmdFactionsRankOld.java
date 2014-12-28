package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.cmd.VisibilityMode;
import com.massivecraft.massivecore.util.MUtil;

public class CmdFactionsRankOld extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
		
	final String rankName;
		
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankOld(String rank)
	{
		this.rankName = rank.toLowerCase();
		
		// Aliases
		this.addAliases(rankName);
	
		// Args
		this.addRequiredArg("player");
		
		this.setVisibilityMode(VisibilityMode.INVISIBLE);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		Factions.get().getOuterCmdFactions().cmdFactionsRank.execute(sender, MUtil.list(arg(0),rankName));
	}
}
