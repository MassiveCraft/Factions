package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsSeeChunk extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSeeChunk()
	{
		// Aliases
		this.addAliases("sc", "seechunk");
		
		// Args
		this.addOptionalArg("active", "toggle");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.SEECHUNK.node));
		this.addRequirements(ReqIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		boolean old = msender.isSeeingChunk();
		boolean targetDefault = !old;
		Boolean target = this.arg(0, ARBoolean.get(), targetDefault);
		if (target == null) return;
		String targetDesc = Txt.parse(target ? "<g>ON": "<b>OFF");
		
		// NoChange
		if (target.equals(old))
		{
			msg("<i>See Chunk is already %s<i>.", targetDesc);
			return;
		}
		
		// Apply
		msender.setSeeingChunk(target);
		
		// Inform
		msg("<i>See Chunk is now %s<i>.", targetDesc);
	}

}
