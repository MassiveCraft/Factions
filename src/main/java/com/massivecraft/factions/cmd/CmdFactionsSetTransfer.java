package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.cmd.arg.ARWorldId;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;


public class CmdFactionsSetTransfer extends CmdFactionsSetXTransfer
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetTransfer()
	{
		// Aliases
		this.addAliases("t", "transfer");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.SET_TRANSFER.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<PS> getChunks()
	{
		// Create Ret
		Set<PS> chunks = null;
		
		// Args
		Faction oldFaction = this.getOldFaction();
		if (oldFaction == null) return null;
		
		if (MUtil.list("a", "al", "all").contains(this.arg(0).toLowerCase()))
		{
			chunks = BoardColl.get().getChunks(oldFaction);
			this.setFormatOne("<h>%s<i> %s all <h>%d <i>chunks.");
			this.setFormatMany("<h>%s<i> %s all <h>%d <i>chunks.");
		}
		else
		{
			String worldId = null;
			if (MUtil.list("map").contains(this.arg(0).toLowerCase()))
			{
				if (me != null)
				{
					worldId = me.getWorld().getName();
				}
				else
				{
					msg("<b>You must specify which map from console.");
					return null;
				}
			}
			else
			{
				worldId = this.arg(0, ARWorldId.get());
				if (worldId == null) return null;
			}
			Board board = BoardColl.get().get(worldId);
			chunks = board.getChunks(oldFaction);
			String worldDisplayName = Mixin.getWorldDisplayName(worldId);
			this.setFormatOne("<h>%s<i> %s all <h>%d <i>chunks in <h>" + worldDisplayName + "<i>.");
			this.setFormatMany("<h>%s<i> %s all <h>%d <i>chunks in <h>" + worldDisplayName + "<i>.");
		}
		
		// Return Ret
		return chunks;
	}
	
}
