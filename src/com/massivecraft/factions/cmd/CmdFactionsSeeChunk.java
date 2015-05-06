package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.MassiveException;
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
		this.addArg(ARBoolean.get(), "active", "toggle");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.SEECHUNK.node));
		this.addRequirements(ReqIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		boolean old = msender.isSeeingChunk();
		boolean targetDefault = !old;
		boolean target = this.readArg(targetDefault);
		String targetDesc = Txt.parse(target ? "<g>ON": "<b>OFF");
		
		// NoChange
		if (target == old)
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
